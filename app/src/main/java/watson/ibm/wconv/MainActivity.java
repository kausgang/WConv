package watson.ibm.wconv;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
//import com.ibm.watson.developer_cloud.conversation.v1.model.Context;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import java.util.ArrayList;
import java.util.List;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //VARIABLE DECLARATION
    private Conversation service;
    private MessageResponse response = null;
    private String conversation_version="2017-05-26";
    private String username="eb0f0d8f-ff2c-4a10-b9c5-991da2b22a89";
    private String password="EI1tsAqmlecX";
    private String workspace = "9f0a85ae-7365-4b49-95d0-114cd2dcfdd7";
    private RecyclerView recyclerview;
    private List<String[]> msg=new ArrayList<String[]>();
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //VARIABLE DECLARATION
        service = new Conversation(conversation_version);
        service.setUsernameAndPassword(username, password);
        recyclerview = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutmanager);
        //CALL WATSON CLASS WITH FIRST MESSAGE
        try {
            boolean first_message = true;
            Watson watson = new Watson(first_message);
            watson.execute(service, response,"",first_message);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Watson extends AsyncTask<Object, Void, MessageResponse> {

        private boolean first_message;
        private String request;
        private MessageResponse p_response;

        //FOR FIRST CALL
        public Watson(boolean first_message) {
            this.first_message = first_message;
        }
        //FOR SUBSEQUENT MESSAGES
        public Watson() {
            this.first_message=false;
        }

        @Override
        protected MessageResponse doInBackground(Object[] objects) {

            //COLLECT CONVERSATION SERVICE, MESSAGE RESPONSE,MESSAGE REQUEST STRING FOR WATSON AND BOOLEAN TO CHECK IF FIRST MESSAGE
            Conversation service = (Conversation) objects[0];
            MessageResponse response = (MessageResponse) objects[1];
            request=(String) objects[2];
            first_message=(boolean)objects[3];

            if (first_message) {
                try {
                    //BUILD REQUEST STRING (RECOMENDED IN WATSON DOCUMENTATION)
                    InputData input = new InputData.Builder("").build();
                    //BUILD MESSAGE OPTION (RECOMENDED IN WATSON DOCUMENTATION)
                    MessageOptions options = new MessageOptions.Builder(workspace).input(input).context(null).build();
                    //EXECUTE
                    response = service.message(options).execute();
                    //SAVE RESPONSE
                    p_response=response;

                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    com.ibm.watson.developer_cloud.conversation.v1.model.Context context=response.getContext();
                    InputData input = new InputData.Builder(request).build();
                    MessageOptions options = new MessageOptions.Builder(workspace).input(input).context(context).build();
                    response = service.message(options).execute();
                    p_response=response;
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            //RETRIEVE MESSAGE RESPONSE
            response=p_response;
            //EXTRACT OUTPIT STRING FROM MESSAGERESPONSE
            String message=response.getOutput().getText().toString();
            //ADD TO LIST - <MESSAGE_TYPE>="watson" is added to identify
            msg.add(new String[]{message,"watson"});

            if(first_message){
                adapter  = new Adapter(msg);
                recyclerview.setAdapter(adapter);
            }
            // SENDMESSAGE() - 8.Inside onPostExecute refresh the recyclerview and scroll to display the last message
            //for subsequent messages
            else{
                adapter.notifyDataSetChanged();
                recyclerview.smoothScrollToPosition(adapter.getItemCount()-1);
            }
        }
    }

    void sendmessage(View view){

        //1.	Collect input message from edittext
        EditText editText=(EditText)findViewById(R.id.edittext_chatbox);
        String request=editText.getText().toString();
        //2.	Set edittext message to “”
        editText.setText("");
        //3.	Add this message to list<String[]> ( {“<MESSAGE_TEXT>”,”user”} )
        msg.add(new String[]{request,"user"});
        //4.	Scroll recyclerview to focus on the last message
        recyclerview.smoothScrollToPosition(adapter.getItemCount()-1);
        //5.	Refresh recycleview
        adapter.notifyDataSetChanged();
        //6. & 7. Create new watson object & call execute method
        (new Watson()).execute(service,response,request,false);
    }


}
