package com.sbit.chatify.entity;

import com.sbit.chatify.constant.CollectionConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = CollectionConstant.FRIEND_REQUEST)
public class FriendRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private ObjectId id;
    private String senderId;
    private String receiverId;
    private Date createdAt;
    private Boolean isAccepted;
    private String message;
}
