package com.opinta.dto.postid;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class PostIdDto {
    private String postId;
    
    public PostIdDto(String postId) {
        this.postId = postId;
    }
}
