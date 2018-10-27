/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.database;

import com.mongodb.client.MongoDatabase;

@FunctionalInterface
public interface MongoTask {

    public void run(MongoDatabase database);

}
