package com.sbit.chatify.entity;

import com.sbit.chatify.constant.CollectionConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = CollectionConstant.CHAT)
public class Chat {
    @Id
    private ObjectId id;
    private String senderId;
    private String receiverId;
    private String message;
    private String type;
    private Date createdAt;
    private Boolean isRead;
    private Boolean isActive;

}
