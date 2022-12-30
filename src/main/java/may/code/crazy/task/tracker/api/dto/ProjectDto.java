package may.code.crazy.task.tracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectDto {

    @NonNull
    @JsonProperty("id")
    Long id;

    @NonNull
    @JsonProperty("name")
    String name;

    @NonNull
    @JsonProperty("created_at")
    Instant createAt;

    @NonNull
    @JsonProperty("updated_at")
    Instant updatedAt;
}
