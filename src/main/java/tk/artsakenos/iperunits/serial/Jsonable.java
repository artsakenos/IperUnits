/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.serial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import tk.artsakenos.iperunits.file.SuperFileText;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Un oggetto Jsonable può fornire una sua descrizione in formato Json. Basta
 * fare un POJO con getter e setter, estendere questa classe e utilizzare i
 * metodi toJson*. Non c'è bisogno di estendere nulla, i metodi vengono chiamati
 * sull'ultima istanza.
 *
 * @author Andrea
 */
@Log
public class Jsonable implements Serializable {

    public JsonNode toJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree(this);
    }

    public String toJsonString() {
        return toJsonNode().toString();
    }

    /**
     * Retrieve a Json Hit by its path. e.g.,
     * <p>
     * String user = hit.get("_source").get("user").get("id").asText();
     * <p>
     * becomes
     * <p>
     * String user = getHit(hit, "_source.user.id").asText();
     *
     * @param jsonNode The JSon Node
     * @param path     The Path if empty or null, all the node will be return.
     * @return a JsonNode from the path
     */
    public static JsonNode getJsonNodeByPath(JsonNode jsonNode, String path) {
        JsonNode output = jsonNode;
        if (path == null || path.trim().isEmpty()) {
            return output;
        }
        for (String p : path.split("\\.")) {
            output = output.get(p);
            if (output == null) {
                return null;
            }
        }
        return output;
    }

    /**
     * Recupera il JsonNode dato il path.
     *
     * @param json Il Json
     * @param path Il path in formato stringa. "" se la root.
     * @return The JsonNode
     */
    public static JsonNode getJsonNodeByPath(String json, String path) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(json);
        } catch (IOException ex) {
            log.severe("getJsonNodeByPath('" + json + "', '" + path + "'): " + ex.getLocalizedMessage());
            return null;
        }
        return getJsonNodeByPath(node, path);
    }

    public void toFile(String filePath) {
        String toJsonString = toJsonString();
        SuperFileText.setText(filePath, toJsonString);
    }

    public void toFile(String filePath, boolean prettyprint) {
        toFile(filePath, this, prettyprint);
    }

    public static void toFile(String filePath, Object object, boolean prettyprint) {
        SuperFileText.setText(filePath, toJson(object, prettyprint));
    }

    /**
     * Può anche essere passato un JsonNode!
     *
     * @param object      The Object
     * @param prettyprint Prettyprint
     * @return The Json of the Object
     */
    public static String toJson(Object object, boolean prettyprint) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = prettyprint
                    ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object)
                    : mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Jsonable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    public static JsonNode toJsonNode(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromFile(String filePath, Class<T> TClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(filePath), TClass);
        } catch (IOException ex) {
            Logger.getLogger(Jsonable.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Se hai un super file json di answer, con un oggetto che contiene un array
     * di elementi, incapsulati in un wrapper, questo metodo te ne estrapola la
     * lista. Pensa ad esempio al blobbone di risposta di elasticsearch.
     * <p>
     * Esempio: List<Slim> fromFile = getFromFile("C:\\Xitler.json", Slim.class,
     * "hits.hits", "_source");
     * <p>
     * TODO, semplificare: <a href="https://www.baeldung.com/jackson-collection-array">come qui</a>
     *
     * @param <T>           La classe dell'oggetto di riferimento.
     * @param filePath      Il path del file
     * @param TClass        La classe di T
     * @param pathToArray   Il path all'array, e.g., hits.hits
     * @param pathToElement il wrapper dell'oggetto, e.g., _source
     * @return La tua lista.
     */
    public static <T> List<T> fromFile(String filePath, Class<T> TClass, String pathToArray, String pathToElement) {
        String json = SuperFileText.getText(filePath);
        return fromJson(json, TClass, pathToArray, pathToElement);
    }

    /**
     * Si può anche usare la strategia generica:
     * <pre>
     * ArrayList<LinkedHashMap> fromJson = Jsonable.fromJson(json, ArrayList.class);
     * for (LinkedHashMap viewItem : fromJson) {
     * </pre>
     *
     * @param <T>    Il tipo restituito
     * @param json   Il json
     * @param TClass La classe di riferimento
     * @return The Object T
     */
    public static <T> T fromJson(String json, Class<T> TClass) {
        ObjectMapper mapper = new ObjectMapper();
        T obj;
        try {
            obj = mapper.readValue(json, TClass);
        } catch (IOException ex) {
            Logger.getLogger(Jsonable.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return obj;
    }

    public static <T> List<T> fromJson(String json, Class<T> TClass, String pathToArray, String pathToElement) {
        LinkedList<T> resultList = new LinkedList<>();
        JsonNode jsonNode = getJsonNodeByPath(json, pathToArray);
        if (jsonNode == null) return resultList;
        Iterator<JsonNode> elements = jsonNode.elements();
        List<JsonNode> list = new ArrayList<>();
        elements.forEachRemaining(list::add);
        for (JsonNode node : list) {
            T fromJson;
            if (pathToElement != null && !pathToElement.isEmpty()) {
                fromJson = fromJson(node.get(pathToElement).toString(), TClass);
            } else {
                fromJson = fromJson(node.toString(), TClass);
            }
            resultList.add(fromJson);
        }
        return resultList;
    }

    /**
     * A naive depth-first search implementation using recursion. Useful *only**
     * for small object graphs. This will be inefficient (stack overflow) for
     * finding deeply-nested needles or needles toward the end of a forest with
     * deeply-nested branches.
     *
     * @param node       The Node
     * @param entityName The Entity Name
     * @return a Json Node
     */
    public static JsonNode searchEntity(JsonNode node, String entityName) {
        if (node == null) {
            return null;
        }
        if (node.has(entityName)) {
            return node.get(entityName);
        }
        if (!node.isContainerNode()) {
            return null;
        }
        for (JsonNode child : node) {
            if (child.isContainerNode()) {
                JsonNode childResult = searchEntity(child, entityName);
                if (childResult != null && !childResult.isMissingNode()) {
                    return childResult;
                }
            }
        }
        // not found fall through
        return null;
    }

}
