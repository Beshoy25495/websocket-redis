package com.bwagih.websocket.repository;

import com.bwagih.websocket.models.Menu;
import com.bwagih.websocket.models.Person;
import com.bwagih.websocket.models.ResponseModel;
import com.bwagih.websocket.service.RedisMessagePublisher;
import com.bwagih.websocket.service.WSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.keyvalue.repository.support.SimpleKeyValueRepository;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.core.mapping.RedisPersistentEntity;
import org.springframework.data.redis.repository.core.MappingRedisEntityInformation;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Repository;


import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Repository
public class MenuRepository {

    public static final String HASH_KEY_NAME = "MENU-ITEM";
    final Logger logger = LoggerFactory.getLogger(MenuRepository.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, Long, Menu> hashOperations;

    private final RedisMappingContext mappingContext = new RedisMappingContext();


    @Autowired
    private RedisMessagePublisher redisMessagePublisher;

    @Autowired
    private WSService wsService;

//    KeyValueOperations keyValueOperations;

    PartialUpdate<Menu> update;

    @PostConstruct
    private void intializeHashOperations() {
        this.hashOperations = redisTemplate.opsForHash();
    }


    public Menu save(Menu menu) {
        // SETS menu object in MENU-ITEM hashmap at menuId key

        if (findItemById(menu.getId()) != null ) {
            wsService.notifyFrontendKeyChanged(HASH_KEY_NAME + " with ID => { " + menu.getId() + " } updated successfully !!");
        } else {
            wsService.notifyFrontendKeyEvent(HASH_KEY_NAME + " with ID => { " + menu.getId() + " } created successfully !!");
        }

//        update = new PartialUpdate<>(menu.getId(), Menu.class);
//        keyValueOperations.update(update);

        hashOperations.put(HASH_KEY_NAME, menu.getId(), menu);
        return menu;
    }

    public List<Menu> findAll() {
        // GET all Menu values
        return hashOperations.values(HASH_KEY_NAME);
    }

    public Menu findItemById(long id) {
        // GET menu object from MENU-ITEM hashmap by menuId key
        return hashOperations.get(HASH_KEY_NAME, id);
    }


    public String deleteMenu(long id) {
        // DELETE the hashkey by menuId from MENU-ITEM hashmap
        if (findItemById(id) == null) {
            return "not found, please make sure from id ..";
        }
        if (hashOperations.delete(HASH_KEY_NAME, id) > 0) {
            wsService.notifyFrontendKeyChanged(HASH_KEY_NAME + " with ID => { " + id + " } deleted successfully !!");

            update = new PartialUpdate<>(id, Menu.class);
//            keyValueOperations.update(update);

            return "Menu deleted successfully !!";
        }
        return "Error Occured..";
    }






//   ===============================/////////////////==========================

    public ResponseModel saveAll(List<Person> tList){
        ResponseModel response = new ResponseModel();

        try {
            SimpleKeyValueRepository<Person, String> repository = new SimpleKeyValueRepository<>(
                    getEntityInformation(Person.class), new KeyValueTemplate(new com.bwagih.websocket.configuration.RedisKeyValueAdapter(redisTemplate)));

            repository.deleteAll();

            repository.saveAll(tList);

        } catch (Exception e) {
            response.setReplyCode("E");
            response.setReplyMessage(e.getMessage());
        }

        return response;
    }


    public List<Person> findAll0(){

        SimpleKeyValueRepository<Person, String> repository = new SimpleKeyValueRepository<>(
                getEntityInformation(Person.class), new KeyValueTemplate(new com.bwagih.websocket.configuration.RedisKeyValueAdapter(redisTemplate)));

        return repository.findAll();
    }

    private <T> MappingRedisEntityInformation<T, String> getEntityInformation(Class<T> entityClass) {
        return new MappingRedisEntityInformation<>(
                (RedisPersistentEntity<T>) mappingContext.getRequiredPersistentEntity(entityClass));
    }


}

