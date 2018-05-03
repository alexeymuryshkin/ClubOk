package dc.clubok.utils;

import com.mongodb.client.model.Sorts;
import lombok.Data;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.Map;

import static dc.clubok.utils.Constants.ERROR_QUERY_PARAMS;
import static dc.clubok.utils.Constants.acceptableOperators;

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
        try {
            Map<String, Document> fieldParams = new HashMap<>();
            map.forEach((k, v) -> {
                String value = map.get(k)[0];

                switch (k) {
                    case "size":
                        setSize(Integer.parseInt(value));
                        break;
                    case "page":
                        setPage(Integer.parseInt(value));
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
                            if (acceptableOperators.contains(params[1])) {
                                if (fieldParams.containsKey(params[0])) {
                                    if (isNumeric(value)) fieldParams.get(params[0]).append("$" + params[1], Integer.parseInt(value));
                                    else fieldParams.get(params[0]).append("$" + params[1], value);
                                } else {
                                    if (isNumeric(value)) fieldParams.put(params[0], new Document("$" + params[1], Integer.parseInt(value)));
                                    else fieldParams.put(params[0], new Document("$" + params[1], value));
                                }
                            } else {
                                Document details = new Document("details", "Operator '" + params[1] + "' is not acceptable");
                                throw new ClubOkException(ERROR_QUERY_PARAMS, details);
                            }
                        }
                        break;
                }
            });

            fieldParams.forEach((k, v) -> {
                fields.append(k, v);
            });
        } catch (NumberFormatException nfe) {
            Document details = new Document("details", "Page and Size parameters should be number");
            throw new ClubOkException(ERROR_QUERY_PARAMS, details);
        }

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