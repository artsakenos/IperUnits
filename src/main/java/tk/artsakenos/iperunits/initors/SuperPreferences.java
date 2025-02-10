/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.initors;

import tk.artsakenos.iperunits.file.FileManager;
import tk.artsakenos.iperunits.serial.Jsonable;
import tk.artsakenos.iperunits.web.SuperElasticsearch;

import java.util.Optional;
import java.util.TreeMap;

/**
 * Una classe per persistere un dizionario. Si usa così:
 * <pre>
 * SuperPreferences.get(...)
 * SuperPreferences.set(...)
 * SuperPreferences.save(); // Se non settato in automatico.
 * </pre>
 * <p>
 * E' possibile cambiare il (SuperParams.PATH) se necessario. Salva
 * automaticamente (automatic_save) se necessario.
 *
 * @author Andrea
 * @version Nov 20, 2020
 */
@SuppressWarnings("unused")
public class SuperPreferences extends Jsonable {

    private final String path;
    private final boolean automatic_save;
    private static TreeMap<String, Object> params = null;

    @SuppressWarnings("unchecked")
    public SuperPreferences(String path, boolean automatic_save) {
        this.path = path;
        this.automatic_save = automatic_save;

        if (FileManager.fileExists(path)) {
            params = (TreeMap<String, Object>) Jsonable.fromFile(path, TreeMap.class);
        } else {
            params = new TreeMap<>();
        }
    }

    public void save() {
        Jsonable.toFile(path, params, true);
    }

    public Object get(String key) {
        return params.get(key);
    }

    public Object get(String key, Object defaultValue) {
        Object get = params.get(key);
        if (get == null) {
            get = defaultValue;
            set(key, get);
        }
        return get;
    }

    public void set(String key, Object value) {
        params.put(key, value);
        if (automatic_save) {
            save();
        }
    }

    private SuperElasticsearch webES;
    private String webIndex;
    private String webType;
    private String webId;

    public void webInit(String esEndpoint, String user, String password, String index, String type, String id) {
        webES = new SuperElasticsearch(esEndpoint, user, password);
        webIndex = index;
        webType = type;
        webId = id;
    }

    public void webSet(String key, Object value) {
        set(key, value);
        if (automatic_save) webSave();
    }

    public Object webGet(String key) {
        @SuppressWarnings("unchecked")
        Optional<TreeMap<String, String>> treeMap = (Optional<TreeMap<String, String>>) (Optional<?>)
                webES.get(webIndex, webType, webId, TreeMap.class);
        treeMap.get().forEach(this::set);
        return get(key);
    }

    public void webSave() {
        webES.index(webIndex, webType, webId, params);
    }

}
