package pl.michal.olszewski.rssaggregator.events;

import java.io.Serializable;

public class NotificationData implements Serializable {

  private long id;
  private String name;
  private String email;
  private String mobile;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Override
  public String toString() {
    return "NotificationData{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", mobile='" + mobile + '\'' +
        '}';
  }
}