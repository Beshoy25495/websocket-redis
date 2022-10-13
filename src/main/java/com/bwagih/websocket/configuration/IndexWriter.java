package com.bwagih.websocket.configuration;

import java.util.Set;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.GeoIndexedPropertyValue;
import org.springframework.data.redis.core.convert.IndexedData;
import org.springframework.data.redis.core.convert.RedisConverter;
import org.springframework.data.redis.core.convert.RemoveIndexedData;
import org.springframework.data.redis.core.convert.SimpleIndexedPropertyValue;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;


class IndexWriter {

    private final RedisConnection connection;
    private final RedisConverter converter;


    public IndexWriter(RedisConnection connection, RedisConverter converter) {

        Assert.notNull(connection, "RedisConnection cannot be null");
        Assert.notNull(converter, "RedisConverter cannot be null");

        this.connection = connection;
        this.converter = converter;
    }


    public void createIndexes(Object key, Iterable<IndexedData> indexValues) {
        createOrUpdateIndexes(key, indexValues, IndexWriteMode.CREATE);
    }

    public void updateIndexes(Object key, Iterable<IndexedData> indexValues) {
        createOrUpdateIndexes(key, indexValues, IndexWriteMode.PARTIAL_UPDATE);
    }

    public void deleteAndUpdateIndexes(Object key, @Nullable Iterable<IndexedData> indexValues) {
        createOrUpdateIndexes(key, indexValues, IndexWriteMode.UPDATE);
    }

    private void createOrUpdateIndexes(Object key, @Nullable Iterable<IndexedData> indexValues,
                                       IndexWriteMode writeMode) {

        Assert.notNull(key, "Key must not be null");
        if (indexValues == null) {
            return;
        }

        byte[] binKey = toBytes(key);

        if (ObjectUtils.nullSafeEquals(IndexWriteMode.UPDATE, writeMode)) {

            if (indexValues.iterator().hasNext()) {
                IndexedData data = indexValues.iterator().next();
                if (data != null) {
                    removeKeyFromIndexes(data.getKeyspace(), binKey);
                }
            }
        } else if (ObjectUtils.nullSafeEquals(IndexWriteMode.PARTIAL_UPDATE, writeMode)) {
            removeKeyFromExistingIndexes(binKey, indexValues);
        }

        addKeyToIndexes(binKey, indexValues);
    }


    public void removeKeyFromIndexes(String keyspace, Object key) {

        Assert.notNull(key, "Key must not be null");

        byte[] binKey = toBytes(key);
        byte[] indexHelperKey = ByteUtils.concatAll(toBytes(keyspace + ":"), binKey, toBytes(":idx"));

        for (byte[] indexKey : connection.sMembers(indexHelperKey)) {

            DataType type = connection.type(indexKey);
            if (DataType.ZSET.equals(type)) {
                connection.zRem(indexKey, binKey);
            } else {
                connection.sRem(indexKey, binKey);
            }
        }

        connection.del(indexHelperKey);
    }


    public void removeAllIndexes(String keyspace) {

        Set<byte[]> potentialIndex = connection.keys(toBytes(keyspace + ":*"));

        if (!potentialIndex.isEmpty()) {
            connection.del(potentialIndex.toArray(new byte[potentialIndex.size()][]));
        }
    }

    private void removeKeyFromExistingIndexes(byte[] key, Iterable<IndexedData> indexValues) {

        for (IndexedData indexData : indexValues) {
            removeKeyFromExistingIndexes(key, indexData);
        }
    }


    protected void removeKeyFromExistingIndexes(byte[] key, IndexedData indexedData) {

        Assert.notNull(indexedData, "IndexedData must not be null");

        Set<byte[]> existingKeys = connection
                .keys(toBytes(indexedData.getKeyspace() + ":" + indexedData.getIndexName() + ":*"));

        if (!CollectionUtils.isEmpty(existingKeys)) {
            for (byte[] existingKey : existingKeys) {

                if (indexedData instanceof GeoIndexedPropertyValue) {
                    connection.geoRemove(existingKey, key);
                } else {
                    connection.sRem(existingKey, key);
                }
            }
        }
    }

    private void addKeyToIndexes(byte[] key, Iterable<IndexedData> indexValues) {

        for (IndexedData indexData : indexValues) {
            addKeyToIndex(key, indexData);
        }
    }


    protected void addKeyToIndex(byte[] key, IndexedData indexedData) {

        Assert.notNull(key, "Key must not be null");
        Assert.notNull(indexedData, "IndexedData must not be null");

        if (indexedData instanceof RemoveIndexedData) {
            return;
        }

        if (indexedData instanceof SimpleIndexedPropertyValue) {

            Object value = ((SimpleIndexedPropertyValue) indexedData).getValue();

            if (value == null) {
                return;
            }

            byte[] indexKey = toBytes(indexedData.getKeyspace() + ":" + indexedData.getIndexName() + ":");
            indexKey = ByteUtils.concat(indexKey, toBytes(value));
            connection.sAdd(indexKey, key);

            // keep track of indexes used for the object
            connection.sAdd(ByteUtils.concatAll(toBytes(indexedData.getKeyspace() + ":"), key, toBytes(":idx")), indexKey);
        } else if (indexedData instanceof GeoIndexedPropertyValue) {

            GeoIndexedPropertyValue geoIndexedData = ((GeoIndexedPropertyValue) indexedData);

            Object value = geoIndexedData.getValue();
            if (value == null) {
                return;
            }

            byte[] indexKey = toBytes(indexedData.getKeyspace() + ":" + indexedData.getIndexName());
            connection.geoAdd(indexKey, geoIndexedData.getPoint(), key);

            // keep track of indexes used for the object
            connection.sAdd(ByteUtils.concatAll(toBytes(indexedData.getKeyspace() + ":"), key, toBytes(":idx")), indexKey);
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot write index data for unknown index type %s", indexedData.getClass()));
        }
    }

    private byte[] toBytes(@Nullable Object source) {

        if (source == null) {
            return new byte[] {};
        }

        if (source instanceof byte[]) {
            return (byte[]) source;
        }

        if (converter.getConversionService().canConvert(source.getClass(), byte[].class)) {
            return converter.getConversionService().convert(source, byte[].class);
        }

        throw new InvalidDataAccessApiUsageException(String.format(
                "Cannot convert %s to binary representation for index key generation; "
                        + "Are you missing a Converter; Did you register a non PathBasedRedisIndexDefinition that might apply to a complex type",
                source.getClass()));
    }

    private static enum IndexWriteMode {

        CREATE, UPDATE, PARTIAL_UPDATE
    }
}
