package com.sbit.chatify.entity;

import com.sbit.chatify.constant.CollectionConstant;
import com.sbit.chatify.model.ContactInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = CollectionConstant.CONTACT)
public class Contact implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private ObjectId id;
    private String userId;
    private List<ContactInfo> contacts;
}
