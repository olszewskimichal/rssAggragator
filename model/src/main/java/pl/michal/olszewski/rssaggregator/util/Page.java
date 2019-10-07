package pl.michal.olszewski.rssaggregator.util;

public class Page {

  private final int limit;
  private final int page;

  public Page(Integer limit, Integer page) {
    this.limit = limit == null ? 50 : limit;
    this.page = page == null ? 1 : page;
  }

  public int getLimit() {
    return limit;
  }

  public int getPageForSearch() {
    return page - 1;
  }

  public int getPageForHuman() {
    return page;
  }

}
