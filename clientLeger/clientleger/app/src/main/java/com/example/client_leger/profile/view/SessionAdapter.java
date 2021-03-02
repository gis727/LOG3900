package com.example.client_leger.profile.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.client_leger.R;
import com.example.client_leger.profile.model.UserSession;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder>{
    private List<UserSession> userSessions = new ArrayList<>();

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        public TextView connectionDate, disconnectionDate;

        public SessionViewHolder(ConstraintLayout v) {
            super(v);
            connectionDate = v.findViewById(R.id.connection_date);
            disconnectionDate = v.findViewById(R.id.disconnection_date);
        }
    }

    public void setUserSessions(List<UserSession> userSessions) {
        this.userSessions = userSessions;
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.session, parent, false);
        SessionViewHolder vh = new SessionViewHolder((ConstraintLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        UserSession userSession = userSessions.get(position);
        holder.connectionDate.setText(userSession.connectionDate);
        holder.disconnectionDate.setText(userSession.disconnectionDate);
    }

    @Override
    public int getItemCount() {
        return userSessions.size();
    }
}
