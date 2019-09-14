package pl.michal.olszewski.rssaggregator.search.items;

import java.util.Map;
import org.elasticsearch.search.SearchHit;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;

public class ScoreResultsMapper extends DefaultResultMapper {

  @Override
  public <T> T mapSearchHit(SearchHit searchHit, Class<T> type) {
    if (!searchHit.hasSource()) {
      return null;
    }

    Map<String, Object> source = searchHit.getSourceAsMap();
    if (!source.containsKey("id") || source.get("id") == null) {
      source.put("id", searchHit.getId());
    }
    source.put("score", searchHit.getScore());

    Object mappedResult = getEntityMapper().readObject(source, type);

    if (mappedResult == null) {
      return null;
    }

    if (type.isInterface()) {
      return getProjectionFactory().createProjection(type, mappedResult);
    }

    return type.cast(mappedResult);
  }
}