package com.example.client_leger.profile.repository;

import android.app.Application;
import android.util.Log;

import com.example.client_leger.SocketSingleton;
import com.example.client_leger.profile.model.User;
import com.example.client_leger.utils.LiveResource;
import com.example.client_leger.utils.MyJson;
import com.example.client_leger.utils.Resource;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

public class UserRepository {
    private static UserRepository instance;

    private SocketSingleton socket;
    private LiveResource<User> user;

    private UserRepository(Application application) {
        user = new LiveResource<>();
        socket = SocketSingleton.get(application);

        socket.OnViewAccount(onViewAccount);
        socket.OnModifyAccount(onModifyAccount);

        socket.ViewAccount("");
    }

    public static UserRepository getInstance(final Application application) {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) {
                    instance = new UserRepository(application);
                }
            }
        }
        return instance;
    }

    public LiveResource<User> getUser() {
        return user;
    }

    public void viewUser(String nickname) {
        socket.ViewAccount(nickname);
    }

    public void modifyUser(String nickname, String name, String surname, String password, String avatar) {
        socket.ModifyAccount(nickname, name, surname, password, avatar);
    }

    Emitter.Listener onViewAccount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            User user = MyJson.parseUser((String) args[0]);
            UserRepository.this.user.postResource(Resource.DataStatus.NONE, user);
        }
    };

    Emitter.Listener onModifyAccount = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            try
            {
                Log.d("onModifyAccount", "NONE: ");
                JSONObject data = new JSONObject((String) args[0]);

                int state = data.getInt("state");

                user.postStatus(Resource.DataStatus.valueOf(state));

            } catch (Exception e) {
                Log.d("onModifyAccountExcept", "NONE: ");
                return;
            }
        }
    };

    public static void setInstanceToNull()
    {
        instance = null;
    }
}