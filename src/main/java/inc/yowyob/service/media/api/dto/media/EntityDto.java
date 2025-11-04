package inc.yowyob.service.media.api.dto.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Classe de base pour les DTOs représentant une entité.
 * Elle fournit des champs d'audit communs comme les dates de création et de mise à jour.
 */
@Data
public abstract class EntityDto {

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

}
