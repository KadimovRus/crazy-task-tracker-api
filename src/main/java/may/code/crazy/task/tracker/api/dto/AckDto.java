package may.code.crazy.task.tracker.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AckDto {

    boolean answer;

    public static AckDto makeDefault(boolean answer) {
        return builder()
                .answer(answer)
                .build();
    }

}
