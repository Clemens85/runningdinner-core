package org.runningdinner.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true, length = 32, name = "naturalKey", nullable = false)
	private String naturalKey;

	@Version
	@Column(name = "VERSION_NR", nullable = false)
	private long versionNo;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedAt;

	public AbstractEntity() {
		String uuid = UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY);
		setNaturalKey(uuid);
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	protected long getVersionNo() {
		return versionNo;
	}

	protected void setVersionNo(long versionNo) {
		this.versionNo = versionNo;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public boolean isNew() {
		return id == null;
	}

	/**
	 * Returns a string (business key) that uniquely identifies this entity independently of its persistence state
	 * 
	 * @return
	 */
	public String getNaturalKey() {
		return naturalKey;
	}

	protected void setNaturalKey(String naturalKey) {
		this.naturalKey = naturalKey;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(31, 7).append(naturalKey).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AbstractEntity)) { // Needed for hibernate because of proxying
			return false;
		}

		AbstractEntity entity = (AbstractEntity)obj;
		return new EqualsBuilder().append(naturalKey, entity.naturalKey).isEquals();
	}
}
