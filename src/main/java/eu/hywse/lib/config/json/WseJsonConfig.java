package eu.hywse.lib.config.json;

import com.google.gson.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public abstract class WseJsonConfig {

    private Gson gson;

    public WseJsonConfig(){
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        defaults();
    }

    public WseJsonConfig(File file){
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if(!file.exists()){
            defaults();
            toFile(file);
            return;
        }
        JsonParser parser = new JsonParser();
        try {
            fromJson(parser.parse(new InputStreamReader(new FileInputStream(file))));
        }catch(Exception ex){ex.printStackTrace();}
    }

    public void toFile(File file){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {

            if(!file.exists()) {
                System.out.println("[i] Creating file: " + (file.createNewFile() ? "success" : "failed"));
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(gson.toJson(toJson()).getBytes());
            fos.flush();
            fos.close();
        }catch(Exception ex){ex.printStackTrace();}
    }

    public abstract JsonElement toJson();
    public abstract void fromJson(JsonElement json);
    public abstract void defaults();

    public Gson getGson() {
        return gson;
    }
}
