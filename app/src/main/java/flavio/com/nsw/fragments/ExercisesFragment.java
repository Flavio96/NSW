package flavio.com.nsw.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Exercise;
import flavio.com.nsw.others.ExercisesCustomAdapter;
import flavio.com.nsw.others.ytDialog;

public class ExercisesFragment extends Fragment {



    private OnFragmentInteractionListener mListener;


    Context context;
    ExercisesCustomAdapter adapter;

    public ExercisesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExercisesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public ExercisesFragment newInstance(String param1, String param2) {
        ExercisesFragment fragment = new ExercisesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
        final View view = inflater.inflate(R.layout.fragment_exercises, container, false);
        ListView list = view.findViewById(R.id.exercises_list);
        final List<Exercise> exercises = new ArrayList<>();

        XmlResourceParser parser = getResources().getXml(R.xml.exercises);
        // Process the XML data
        try{
            processXMLData(parser, exercises);
        }catch(IOException e){
            e.printStackTrace();
        }catch (XmlPullParserException e){
            // This exception is thrown to signal XML Pull Parser related faults.
            e.printStackTrace();
        }

        adapter = new ExercisesCustomAdapter(exercises, getActivity().getApplicationContext());
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    Intent i = new Intent(getActivity(), ytDialog.class);
                    i.putExtra("url", exercises.get(position).getUrl());
                    i.putExtra("sec", exercises.get(position).getSeconds());
                    startActivity(i);
            }
        });

        return view;
    }

    public static List<Exercise> processXMLData(XmlResourceParser parser, List<Exercise> exercises)throws IOException,XmlPullParserException{
        int eventType = -1;
        // Loop through the XML data
        while(eventType!=parser.END_DOCUMENT){
            if(eventType == XmlResourceParser.START_TAG){
                Exercise e = new Exercise();
                String element = parser.getName();
                if(element.equals("exercise")){
                    if(!parser.getAttributeValue(null,"name").isEmpty()) {
                        e.setId(Integer.parseInt(parser.getAttributeValue(null, "id")));
                        e.setName(parser.getAttributeValue(null, "name"));
                        e.setMuscles(parser.getAttributeValue(null, "muscles"));
                        e.setUrl(parser.getAttributeValue(null, "url"));
                        e.setSeconds(Integer.parseInt(parser.getAttributeValue(null, "seconds")));
                        exercises.add(e);
                    }
                }

            }
            /*
                The method next() advances the parser to the next event. The int value returned from
                next determines the current parser state and is identical to the value returned
                from following calls to getEventType ().

                The following event types are seen by next()

                    START_TAG
                        An XML start tag was read.
                    TEXT
                        Text content was read; the text content can be retrieved using the getText()
                        method. (when in validating mode next() will not report ignorable
                        whitespace, use nextToken() instead)
                    END_TAG
                        An end tag was read
                    END_DOCUMENT
                        No more events are available
            */
            eventType = parser.next();
        }

        return exercises;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
