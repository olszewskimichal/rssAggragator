package pl.michal.olszewski.rssaggregator.blog.failure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class UpdateBlogFailureCount {

  private String blogId;
  private String errorMsg;
  private Long total;
}
