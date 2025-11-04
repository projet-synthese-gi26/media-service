package inc.yowyob.service.media.infrastructure.persistence.entities;


import inc.yowyob.service.media.infrastructure.config.DatabaseConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(DatabaseConfig.MEDIA_TABLE)
public class Media extends Entity {
    @Id
    @Column("id")
    private UUID id;

    @Column("service")
    private String service;

    @Column("name")
    private String name;

    @Column("real_name")
    private String realName;

    @Column("size")
    private Long size;

    @Column("mime")
    private String mime;

    @Column("extension")
    private String extension;

    @Column("path")
    private String path;

    @Column("uri")
    private String uri;

}
