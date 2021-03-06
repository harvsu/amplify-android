/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amplifyframework.hub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.UUID;

/**
 * An event is the top-level data envelope that gets passed around on
 * the Hub.  Events are published onto the hub, and subscribers
 * subscribe to the hub to receive events.
 * @param <T> Type of data in the event
 */
public final class HubEvent<T> {
    private final String name;
    private final T data;
    private final UUID uuid;

    /**
     * Construct a Hub event.
     * @param name The name of this event
     * @param data The data of the event generated by the operation
     */
    private HubEvent(@NonNull String name, @Nullable T data) {
        this.name = name;
        this.data = data;
        this.uuid = UUID.randomUUID();
    }

    /**
     * Creates a hub event with a given name, and no data.
     * @param name Name for the event
     * @return A Hub Event with a name and no data.
     */
    @NonNull
    public static HubEvent<?> create(@NonNull String name) {
        return new HubEvent<>(name, null);
    }

    /**
     * Create a Hub event from an enumerated value.
     * The {@link Enum#toString()} method will be used to generate the event name.
     * @param enumerated An enumerated value
     * @param <E> Type of enumeration
     * @return A Hub event with the enumerate value's string representation as the event name
     */
    @NonNull
    public static <E extends Enum<E>> HubEvent<?> create(@NonNull E enumerated) {
        Objects.requireNonNull(enumerated);
        return new HubEvent<>(enumerated.toString(), null);
    }

    /**
     * Creates a Hub event with a name and associated data.
     * @param name Name for the event
     * @param data Data associated with the event
     * @param <T> Type of data in the event
     * @return A Hub event
     */
    @NonNull
    public static <T> HubEvent<T> create(@NonNull String name, @NonNull T data) {
        return new HubEvent<>(name, data);
    }

    /**
     * Creates a Hub event from an enum (to generate the event name) and a data item.
     * @param enumerated An enumeration value
     * @param data Data to populate in event
     * @param <E> An enumeration type, {@link Enum#toString()} will be used to generate the event name
     * @param <T> Type of event data
     * @return A HubEvent
     */
    @NonNull
    public static <T, E extends Enum<E>> HubEvent<T> create(@NonNull E enumerated, @NonNull T data) {
        Objects.requireNonNull(enumerated);
        Objects.requireNonNull(data);
        return new HubEvent<>(enumerated.toString(), data);
    }

    /**
     * Gets the name, tag, or grouping of the HubEvent. Recommended to be a small string without spaces,
     * such as `signIn` or `hang_up`. For AmplifyOperations, this will be a concatenation of the
     * category display name and a short name of the operation type, as in "Storage.getURL" or
     * "Storage.downloadFile".
     * @return Event name
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Gets a free-form structure used to pass objects or custom data. For HubEvents that are generated
     * from AmplifyOperations, this field will be the Operation's associated AsyncEvent.
     * @return Event data
     */
    @Nullable
    public T getData() {
        return data;
    }

    /**
     * Gets a unique identifier that identifies the event.
     * @return Unique ID for this event
     */
    @NonNull
    public UUID getId() {
        return uuid;
    }

    /**
     * Publish the instance of the {@link HubEvent} to Amplify Hub.
     * @param channel The channel to publish the event to.
     * @param hub A reference to the Hub category.
     */
    public void publish(@NonNull HubChannel channel, @NonNull HubCategoryBehavior hub) {
        hub.publish(channel, this);
    }

    @NonNull
    @Override
    public String toString() {
        return "HubEvent{" +
                "name='" + name + '\'' +
                ", data=" + data +
                ", uuid=" + uuid +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object thatObject) {
        if (this == thatObject) {
            return true;
        }
        if (thatObject == null || getClass() != thatObject.getClass()) {
            return false;
        }

        HubEvent<?> that = (HubEvent<?>) thatObject;

        if (!ObjectsCompat.equals(name, that.name)) {
            return false;
        }
        if (!ObjectsCompat.equals(data, that.data)) {
            return false;
        }
        return ObjectsCompat.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }

    /**
     * Interface that should be implemented by any type
     * used as an event payload.
     * @param <T> A class representing the event payload.
     *
     */
    public interface Data<T> {

        /**
         * An implementation of this method should create an instance of
         * {@link HubEvent} with using itself as the {@link HubEvent#data} field.
         * @return An instance of {@link HubEvent}
         */
        HubEvent<T> toHubEvent();
    }
}
