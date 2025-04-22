package com.sbit.chatify.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "UserDetail")
public class UserDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    private String name;
    private Integer age;
    private ObjectId userId;
    private String profileImage;
}
