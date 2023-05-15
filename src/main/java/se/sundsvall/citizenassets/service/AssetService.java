package se.sundsvall.citizenassets.service;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import se.sundsvall.citizenassets.api.model.Asset;
import se.sundsvall.citizenassets.api.model.AssetCreateRequest;
import se.sundsvall.citizenassets.api.model.AssetSearchRequest;
import se.sundsvall.citizenassets.api.model.AsssetUpdateRequest;
import se.sundsvall.citizenassets.integration.db.AssetRepository;
import se.sundsvall.citizenassets.integration.db.specification.AssetSpecification;
import se.sundsvall.citizenassets.service.mapper.Mapper;

@Service
public class AssetService {

    private final AssetRepository repository;
    private final Mapper mapper;

    private final AssetSpecification specification;

    public AssetService(AssetRepository repository, Mapper mapper, AssetSpecification specification) {
        this.repository = repository;
        this.mapper = mapper;
        this.specification = specification;
    }

    public List<Asset> getAssets(AssetSearchRequest request) {
        return repository.findAll(specification.createAssetSpecification(request))
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    public String createAsset(AssetCreateRequest request) {
        UUID result;
        try {
            var entity = mapper.toEntity(request);

            result = repository.save(entity).getId();
        } catch (DataIntegrityViolationException e) {


            /* This is probably a bit bold but the only constraints we have are on the ID column
               and the assetId column. The ID column is generated by the database and the assetId
               column is generated by the user. So if we get a constraint violation it is
               most likely because the user has tried to create an asset with an assetId that
               already exists.*/
            throw Problem.builder()
                .withTitle("Asset already exists")
                .withDetail("Asset with assetId %s already exists".formatted(request.getAssetId()))
                .withStatus(BAD_REQUEST)
                .build();
        }
        return String.valueOf(result);
    }

    public void deleteAsset(UUID id) {
        repository.deleteById(id);
    }

    public void updateAsset(UUID partyId, String assetId, AsssetUpdateRequest request) {

        var old = repository.findByAssetId(assetId)
            .filter(asset -> asset.getPartyId().equals(partyId))
            .orElseThrow(() ->
                Problem.builder()
                    .withStatus(NOT_FOUND)
                    .withTitle("Asset not found")
                    .withDetail("Asset with assetId %s not found".formatted(assetId))
                    .build());
        repository.save(mapper.updateEntity(old, request));
    }
}
