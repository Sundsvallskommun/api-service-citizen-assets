package se.sundsvall.citizenassets.api;

import static org.springframework.http.HttpHeaders.LOCATION;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.problem.Problem;

import se.sundsvall.citizenassets.api.model.Asset;
import se.sundsvall.citizenassets.api.model.AssetCreateRequest;
import se.sundsvall.citizenassets.api.model.AssetSearchRequest;
import se.sundsvall.citizenassets.api.model.AsssetUpdateRequest;
import se.sundsvall.citizenassets.service.AssetService;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/assets", produces = MediaType.APPLICATION_JSON_VALUE)
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Problem.class)))
@Tag(name = "Assets")
public class AssetResource {
    private final AssetService service;

    public AssetResource(AssetService service) {
        this.service = service;
    }

    @GetMapping
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Asset.class)))
    public ResponseEntity<List<Asset>> getAssets( @ParameterObject @Valid AssetSearchRequest request) {
        return ResponseEntity.ok(service.getAssets( request));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "201", description = "Created - Successful operation", headers = @Header(name = LOCATION, description = "Location of the created resource."))
    public ResponseEntity<String> createAsset(@Valid @RequestBody AssetCreateRequest asset, UriComponentsBuilder uriComponentsBuilder) {
        var result = service.createAsset(asset);
        return ResponseEntity
            .created(uriComponentsBuilder
                .path("/asset/{id}")
                .buildAndExpand(result)
                .toUri())
            .build();
    }

    @PutMapping(path = "{partyid}/{assetid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "204", description = "No content - Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class)))
    public ResponseEntity<Void> updateAsset(@PathVariable("partyid") @ValidUuid UUID partyId,  @NotEmpty  @PathVariable("assetid") String assetid, @Valid @RequestBody AsssetUpdateRequest asset) {
        service.updateAsset(partyId, assetid, asset);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "{id}")
    @ApiResponse(responseCode = "204", description = "No content - Successful operation")
    public ResponseEntity<Void> deleteAsset(@PathVariable("id") @ValidUuid UUID id) {
        service.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }


}
