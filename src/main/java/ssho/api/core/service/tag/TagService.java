package ssho.api.core.service.tag;

import ssho.api.core.domain.tag.model.Tag;

import java.io.IOException;
import java.util.List;

public interface TagService {
    void save(List<String> tagNameList) throws IOException;
    List<Tag> getTagList();
    Tag getTagByName(String tagName);
}
