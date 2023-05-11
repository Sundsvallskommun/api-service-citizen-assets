package se.sundsvall.citizenassets.integration.db.specification;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import se.sundsvall.citizenassets.api.model.AssetRequest;
import se.sundsvall.citizenassets.integration.db.model.AssetEntity;
import se.sundsvall.citizenassets.integration.db.model.AssetEntity_;


@Component
public class AssetSpecification {
    public Specification<AssetEntity> createAssetSpecification(AssetRequest request) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();


            predicates.add(criteriaBuilder.equal(root.get(AssetEntity_.PARTY_ID), request.getPartyId()));

            if(!CollectionUtils.isEmpty(request.getCaseReferenceIds())) {
                predicates.add(criteriaBuilder.in(root.get(AssetEntity_.CASE_REFERENCE_IDS)).value(request.getCaseReferenceIds().get(0)));

            }

            if (StringUtils.isNotBlank(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get(AssetEntity_.TYPE), request.getType()));
            }
            if (request.getIssued() != null) {
                predicates.add(criteriaBuilder.equal(root.get(AssetEntity_.ISSUED), request.getIssued()));
            }

            if (request.getValidTo() != null) {
                predicates.add(criteriaBuilder.equal(root.get(AssetEntity_.VALID_TO), request.getValidTo()));
            }

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get(AssetEntity_.STATUS), request.getStatus()));
            }
            if (StringUtils.isNotBlank(request.getDescription())) {
                predicates.add(criteriaBuilder.equal(root.get(AssetEntity_.DESCRIPTION), request.getDescription()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
