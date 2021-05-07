package com.future.webcollector.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="zb_infos")
public class ZbInfos implements Serializable {

  private static final long serialVersionUID = 7207780155261265206L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  @Column(name = "zb_pro_name")
  private String zbProName;
  @Column(name = "zb_pro_no")
  private String zbProNo;
  @Column(name = "zb_bd_name")
  private String zbBdName;
  @Column(name = "zb_bd_no")
  private String zbBdNo;
  @Column(name = "zb_budget")
  private String zbBudget;
  @Column(name = "zb_start_date")
  private String zbStartDate;
  @Column(name = "zb_end_date")
  private String zbEndDate;
  @Column(name = "create_user")
  private String createUser;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public String getZbProName() {
    return zbProName;
  }

  public void setZbProName(String zbProName) {
    this.zbProName = zbProName;
  }


  public String getZbProNo() {
    return zbProNo;
  }

  public void setZbProNo(String zbProNo) {
    this.zbProNo = zbProNo;
  }


  public String getZbBdName() {
    return zbBdName;
  }

  public void setZbBdName(String zbBdName) {
    this.zbBdName = zbBdName;
  }


  public String getZbBdNo() {
    return zbBdNo;
  }

  public void setZbBdNo(String zbBdNo) {
    this.zbBdNo = zbBdNo;
  }


  public String getZbBudget() {
    return zbBudget;
  }

  public void setZbBudget(String zbBudget) {
    this.zbBudget = zbBudget;
  }


  public String getZbStartDate() {
    return zbStartDate;
  }

  public void setZbStartDate(String zbStartDate) {
    this.zbStartDate = zbStartDate;
  }


  public String getZbEndDate() {
    return zbEndDate;
  }

  public void setZbEndDate(String zbEndDate) {
    this.zbEndDate = zbEndDate;
  }


  public String getCreateUser() {
    return createUser;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

}
