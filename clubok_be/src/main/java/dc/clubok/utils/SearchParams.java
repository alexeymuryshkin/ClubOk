package dc.clubok.utils;

import com.mongodb.client.model.Sorts;
import lombok.Data;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.Map;

@Data
public class SearchParams {
    private int size;
    private int page;
    private Document fields;
    private Bson sort;
    private Bson projection;

    public SearchParams() {
        size = 20;
        page = 1;
        fields = new Document();
    }

    public SearchParams(Map<String, String[]> map) throws ClubOkException {
        this();
        Map<String, Document> fieldParams = new HashMap<>();
        map.forEach((k, v) -> {
            String value = map.get(k)[0];

            switch (k) {
                case "size":
                    if (isNumeric(value)) setSize(Integer.parseInt(value));
                    break;
                case "page":
                    if (isNumeric(value)) setPage(Integer.parseInt(value));
                    break;
                case "sort_by":
                    String[] sorting = value.split("\\.");
                    if (sorting.length == 1) {
                        setSort(sorting[0]);
                    } else if (sorting.length == 2) {
                        setSort(sorting[0], sorting[1]);
                    }
                    break;
                default:
                    String[] params = k.split("\\.");
                    if (params.length == 1) {
                        if (isNumeric(value)) fields.append(params[0], Integer.parseInt(value));
                        else fields.append(params[0], value);
                    } else if (params.length == 2) {
                        if (fieldParams.containsKey(params[0])) {
                            if (isNumeric(value)) fieldParams.get(params[0]).append("$" + params[1], Integer.parseInt(value));
                            else fieldParams.get(params[0]).append("$" + params[1], value);
                        } else {
                            if (isNumeric(value)) fieldParams.put(params[0], new Document("$" + params[1], Integer.parseInt(value)));
                            else fieldParams.put(params[0], new Document("$" + params[1], value));
                        }
                    }
                    break;
            }
        });

        fieldParams.forEach((k, v) -> {
            fields.append(k, v);
        });
    }

    public void setSort(String fieldname, String order) {
        if (order.equals("desc")) {
            this.sort = Sorts.descending(fieldname);
        } else {
            this.sort = Sorts.ascending(fieldname);
        }
    }

    public void setSort(String fieldname) {
        setSort(fieldname, "asc");
    }

    public boolean isNumeric(String s) {
        try {
            int x = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}