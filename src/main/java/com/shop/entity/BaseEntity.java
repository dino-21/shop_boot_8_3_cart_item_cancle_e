package com.shop.entity;



/**
 * BaseEntity 클래스는 엔티티의 생성자와 수정자를 나타내는 정보를 추가로 관리한다.
 * */

//엔티티의 변화를 감지하고 처리하는 리스너를 등록,  즉, 엔티티에 변화가 생길 때마다 해당 리스너가 실행된다.

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@EntityListeners(value = {AuditingEntityListener.class})
//공통 매핑 정보가 필요할 때 사용하는 어노테이션으로,
// 부모 클래스를 상속받는 자식 클래스에 매핑 정보만 제공한다.
@MappedSuperclass
@Getter
public abstract class BaseEntity  extends BaseTimeEntity{
    @CreatedBy  //엔티티를 생성할 때 사용자 정보를 자동으로 저장
    @Column(updatable = false) //엔티티가 수정되어도 해당 필드는 변경되지 않는다.
    private String createdBy;

    @LastModifiedBy //엔티티를 수정한 사용자를 나타내는 정보, 엔티티를 수정할 때 사용자 정보를 자동으로 저장
    private String modifiedBy;
}
