package inc.yowyob.service.media.infrastructure.persistence.entities;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class Entity {

    @ReadOnlyProperty
    @Column("created_at")
    private LocalDateTime createdAt;

    @ReadOnlyProperty
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("deleted_at")
    private LocalDateTime deletedAt;

    @Column("created_by")
    private UUID createdBy;

    @Column("updated_by")
    private UUID updatedBy;

    @Column("deleted_by")
    private UUID deletedBy;
}