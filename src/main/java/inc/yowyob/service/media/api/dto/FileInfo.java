package inc.yowyob.service.media.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class FileInfo {
    @JsonProperty(value = "original_file_name")
    private String originalFileName;

    @JsonProperty(value = "dfs_file_name")
    private String dfsFileName;

    @JsonProperty(value = "dfs_bucket")
    private String dfsBucket;

    @JsonProperty(value = "original_file_name")
    private Instant createdAt;
}
