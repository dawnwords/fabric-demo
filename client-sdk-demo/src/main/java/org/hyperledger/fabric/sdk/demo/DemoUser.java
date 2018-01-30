package org.hyperledger.fabric.sdk.demo;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

class DemoUser implements User {

	private String name;
	private String msp;
	private Enrollment enrollment;

	DemoUser(String name, String msp, Enrollment enrollment) {
		this.name = name;
		this.msp = msp;
		this.enrollment = enrollment;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<String> getRoles() {
		return Collections.emptySet();
	}

	@Override
	public String getAccount() {
		return name;
	}

	@Override
	public String getAffiliation() {
		return msp;
	}

	@Override
	public Enrollment getEnrollment() {
		return enrollment;
	}

	@Override
	public String getMspId() {
		return msp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DemoUser demoUser = (DemoUser) o;
		return Objects.equals(name, demoUser.name) &&
				Objects.equals(msp, demoUser.msp) &&
				Objects.equals(enrollment, demoUser.enrollment);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, msp, enrollment);
	}

	@Override
	public String toString() {
		return "DemoUser{" +
				"name='" + name + '\'' +
				", msp='" + msp + '\'' +
				", enrollment=" + enrollment +
				'}';
	}
}
