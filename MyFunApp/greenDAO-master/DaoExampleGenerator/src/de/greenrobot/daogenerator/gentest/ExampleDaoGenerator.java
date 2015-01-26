/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.daogenerator.gentest;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;



/**
 * Generates entities and DAOs for the example project DaoExample.
 * 
 * Run it as a Java application (not Android).
 * 
 * @author Markus
 */
public class ExampleDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(100, "de.greenrobot.daoMyFun");

        addNote(schema);

        new DaoGenerator().generateAll(schema, "../../NewFun/src");
    }

    private static void addNote(Schema schema) {
        Entity tribe_ship = schema.addEntity("Tribe_Relationship");
        tribe_ship.addIdProperty().autoincrement().primaryKeyAsc();
        tribe_ship.addStringProperty("normal_id").unique().notNull();
        tribe_ship.addStringProperty("hx_id").unique().notNull();
        tribe_ship.addStringProperty("present_name").notNull();
        tribe_ship.addBooleanProperty("is_group").notNull();
        tribe_ship.addStringProperty("image_path");
        tribe_ship.addLongProperty("location").notNull();
        
        Entity conversationEntity = schema.addEntity("Conversation_tribe");
        conversationEntity.addIdProperty().autoincrement().primaryKeyAsc();
        conversationEntity.addStringProperty("con_id").unique();
        conversationEntity.addStringProperty("user_id");
        conversationEntity.addStringProperty("image");
        conversationEntity.addStringProperty("group_id");
        conversationEntity.addIntProperty("status");
        conversationEntity.addLongProperty("time");
        conversationEntity.addIntProperty("type");
        
        Entity conversationmsgEntity = schema.addEntity("Conversation_msg");
        conversationmsgEntity.addIdProperty().autoincrement().primaryKeyAsc();
        conversationmsgEntity.addStringProperty("con_id");
        conversationmsgEntity.addStringProperty("msg_id");
        conversationmsgEntity.addStringProperty("user_id");
        conversationmsgEntity.addStringProperty("group_id");
        conversationmsgEntity.addIntProperty("status");
        conversationmsgEntity.addLongProperty("time");
        
        Entity dianPingDataEntity = schema.addEntity("DianPingData");
        dianPingDataEntity.addIdProperty().autoincrement().primaryKeyAsc();
        dianPingDataEntity.addStringProperty("type");
        dianPingDataEntity.addStringProperty("data");
        dianPingDataEntity.addDateProperty("update_time");
    }


}
