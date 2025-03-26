package dev.fastball.core.field;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    private String url;

    private String name;

    private String type;

    private ImageInfo imageInfo;
}
