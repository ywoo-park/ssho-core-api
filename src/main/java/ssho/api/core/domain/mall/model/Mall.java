package ssho.api.core.domain.mall.model;

import lombok.Data;
import ssho.api.core.domain.tag.model.Tag;

import java.util.List;

@Data
public class Mall {
    private String id;
    private String name;
    private List<Category> categoryList;
    private List<Tag> tagList;
    private String lastSyncTime;
}
