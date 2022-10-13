package com.bwagih.websocket.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    //Used to speed up the property based search
    @Indexed
    private int redisExtId;

    private String item;
    private long price;
}
