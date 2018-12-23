package com.itrus.ikey.safecenter.TOPMFA.widget.view;

import com.itrus.ikey.safecenter.TOPMFA.entitiy.GroupStatusEntity;

import java.util.Comparator;

public class DateComparator implements Comparator<GroupStatusEntity> {

	@Override
	public int compare(GroupStatusEntity lhs, GroupStatusEntity rhs) {
		return rhs.getGroupName().compareTo(lhs.getGroupName());
	}

}
