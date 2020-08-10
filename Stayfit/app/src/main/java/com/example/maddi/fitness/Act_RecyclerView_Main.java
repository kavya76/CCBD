package com.example.maddi.fitness;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;

public class Act_RecyclerView_Main extends Fragment {

    private static final String ARG_MOVIE = "R.id.mdf_main_replace";
    public static String voice_query = "";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    ActDataJson actData;
    private FloatingActionButton voice;
    private RecyclerView mRecyclerView;
    private Act_MyRecyclerViewAdapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public Act_RecyclerView_Main() {
        // Constructor
    }

    public static Act_RecyclerView_Main newInstance() {
        Act_RecyclerView_Main fragment = new Act_RecyclerView_Main();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE, "R.id.rcmain");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        actData = new ActDataJson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_recyclerview_activity, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new Act_MyRecyclerViewAdapter(getActivity(), actData.ActList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        // Animation
        mRecyclerView.setItemAnimator(new ScaleInBottomAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(100);
        mRecyclerView.getItemAnimator().setRemoveDuration(1000);
        mRecyclerView.getItemAnimator().setMoveDuration(100);
        mRecyclerView.getItemAnimator().setChangeDuration(100);
        ScaleInBottomAnimator animator = new ScaleInBottomAnimator();
        mRecyclerView.setItemAnimator(animator);
        // Adapter Animation
        mRecyclerView.setAdapter(new ScaleInAnimationAdapter(mRecyclerViewAdapter));
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(mRecyclerViewAdapter);
        alphaAdapter.setDuration(500);
        mRecyclerView.setAdapter(alphaAdapter);
        voice = (FloatingActionButton) rootView.findViewById(R.id.vsfb);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        return rootView;
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(), "Not Supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voice_query = (result.get(0));
                    Log.d("voice", voice_query);
                }
                break;
            }

        }
    }


    // Search action menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_actionview, menu);
        super.onCreateOptionsMenu(menu, inflater);

        final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (search != null) {
            search.setIconifiedByDefault(true);
            search.setQuery(voice_query, true);
            // search.setQueryHint(voice_query);
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String activity = query.replace(" ", "");
                    MyDownloadJsonAsyncTask downloadJson = new MyDownloadJsonAsyncTask(mRecyclerViewAdapter);
                    downloadJson.execute(activity);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    // Load Async Food Data from Nutrionix.com
    private class MyDownloadJsonAsyncTask extends AsyncTask<String, Void, ActDataJson> {
        private final WeakReference<Act_MyRecyclerViewAdapter> adapterReference;

        public MyDownloadJsonAsyncTask(Act_MyRecyclerViewAdapter adapter) {
            adapterReference = new WeakReference<Act_MyRecyclerViewAdapter>(adapter);
        }

        @Override
        protected ActDataJson doInBackground(String... query) {
            ActDataJson threadMovieData = new ActDataJson();
            for (String act_query : query) {
                try {
                    threadMovieData.populateActDataJson(act_query, Act_RecyclerView_Main.this.getContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return threadMovieData;
        }

        @Override
        protected void onPostExecute(ActDataJson threadMovieData) {
            actData.ActList.clear();
            for (int i = 0; i < threadMovieData.getSize(); i++)
                actData.ActList.clear();
            for (int k = 0; k < threadMovieData.getSize(); k++)
                actData.ActList.add(threadMovieData.ActList.get(k));
            if (adapterReference != null) {
                final Act_MyRecyclerViewAdapter adapter = adapterReference.get();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

}
