package com.zbkj.crmeb.export.controller;

import com.zbkj.crmeb.export.utils.ExcelConstants;
import com.zbkj.crmeb.export.utils.ExcelProperty;

import java.util.Date;

/**
 * 导出excel相关的测试实体类
 */
public class Student implements java.io.Serializable {

    @ExcelProperty(name = "序号")
    private Integer id;
    @ExcelProperty(name = "名称")
    private String name;
    @ExcelProperty(name = "年龄")
    private Integer age;
    @ExcelProperty(name = "性别", replace = {"0_女", "1_男"})
    private Integer sex;
    @ExcelProperty(name = "生日", dateFormat = ExcelConstants.YYYY_MM_DD_HH_MM_SS)
    private Date birth;
    @ExcelProperty(name = "家庭地址")
    private String address;

    public Student() {
        super();
    }

    public Student(Integer id, String name, Integer age, Integer sex, Date birth, String address) {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.birth = birth;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", birth=" + birth +
                ", address='" + address + '\'' +
                '}';
    }
}
