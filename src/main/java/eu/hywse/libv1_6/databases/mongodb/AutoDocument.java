/*
 * Copyright (c) 2018.
 * Created at 19.10.2018
 * ---------------------------------------------
 * @author hyWse
 * @see https://hywse.eu
 * ---------------------------------------------
 * If you have any questions, please contact
 * E-Mail: admin@hywse.eu
 * Discord: hyWse#0126
 */

package eu.hywse.libv1_6.databases.mongodb;

import org.bson.Document;

import java.lang.reflect.Field;

public abstract class AutoDocument<T> {

    public Document getDocument() {
        Document document = new Document();

        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(DocField.class)) continue;
            field.setAccessible(true);

            DocField opt = field.getAnnotation(DocField.class);
            String key = opt.key().length() == 0 ? field.getName() : opt.key();

            try {
                document.append(key, field.get(this));
            } catch (IllegalAccessException e) {
                System.out.println("Can't create doc @ " + getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        return document;
    }

    protected T fromDocument(Document document, Class<T> clazz) {
        T res;

        try {
            res = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        for (Field field : res.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(DocField.class)) continue;
            field.setAccessible(true);

            DocField opt = field.getAnnotation(DocField.class);
            String key = opt.key().length() == 0 ? field.getName() : opt.key();

            if (!document.containsKey(key)) {
                System.out.println("Could not find " + key + " in document! Using default");
            } else {
                try {
                    field.set(res, document.get(key));
                } catch (IllegalAccessException e) {
                    System.out.println("Can't fill " + key + ": " + e.getMessage());
                }
            }
        }

        return res;
    }

}
