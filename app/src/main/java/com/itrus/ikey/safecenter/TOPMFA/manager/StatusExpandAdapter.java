package com.itrus.ikey.safecenter.TOPMFA.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.ChildStatusEntity;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.GroupStatusEntity;

import java.util.List;

/**
 * Created by STAR on 2016/8/17.
 */

public class StatusExpandAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater = null;
    private List<GroupStatusEntity> groupList;

    public StatusExpandAdapter(Context context,
                               List<GroupStatusEntity> group_list) {
        this.groupList = group_list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 返回一级Item总数
     */
    @Override
    public int getGroupCount() {

        return groupList == null ? 0 : groupList.size();
    }

    /**
     * 返回二级Item总数
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return groupList == null ? 0
                : (groupList.get(groupPosition) == null ? 0 : (groupList
                .get(groupPosition).getChildList() == null ? 0
                : groupList.get(groupPosition).getChildList().size()));
    }

    /**
     * 获取一级Item内容
     */
    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groupList.get(groupPosition);
    }

    /**
     * 获取二级Item内容
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        GroupViewHolder holder = new GroupViewHolder();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group_status_item, null);
        }
        holder.groupName = (TextView) convertView
                .findViewById(R.id.one_status_name);

        holder.groupName.setText(groupList.get(groupPosition).getGroupName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder = null;
        ChildStatusEntity entity = (ChildStatusEntity) getChild(groupPosition,
                childPosition);
        if (convertView != null) {
            viewHolder = (ChildViewHolder) convertView.getTag();
        } else {
            viewHolder = new ChildViewHolder();
            convertView = inflater.inflate(R.layout.child_status_item, null);
            viewHolder.tv_login_time = (TextView) convertView.findViewById(R.id.tv_login_time);
            viewHolder.tv_login_type = (TextView) convertView.findViewById(R.id.tv_login_type);
            viewHolder.tv_login_pos = (TextView) convertView.findViewById(R.id.tv_login_pos);

        }
        viewHolder.tv_login_time.setText("12:28");
        viewHolder.tv_login_type.setText("登录方式");
        viewHolder.tv_login_pos.setText("北京");
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupViewHolder {
        TextView groupName;
    }

    private class ChildViewHolder {
        public TextView tv_login_time;
        public TextView tv_login_type;
        public TextView tv_login_pos;
    }

}
