package ssho.api.core.domain.tag.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Tag {
    private ExpTag expTag;
    private List<RealTag> realTagList;
}
