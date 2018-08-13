package tokyo.tkw.thinmp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import tokyo.tkw.thinmp.player.MusicController;
import tokyo.tkw.thinmp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {
    private View mFragmentView;
    private LinearLayout mFooterView;
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_player, container, false);

        setView();
        setListener();

        MusicController.setPlayerView(this);

        return mFragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setView() {
        mFooterView = mFragmentView.findViewById(R.id.footer);
        mPlayButton = mFragmentView.findViewById(R.id.play);
        mPauseButton = mFragmentView.findViewById(R.id.pause);
        mPrevButton = mFragmentView.findViewById(R.id.prev);
        mNextButton = mFragmentView.findViewById(R.id.next);
    }

    private void setListener() {
        mPlayButton.setOnClickListener(new playClickListener());
        mPauseButton.setOnClickListener(new PauseClickListener());
        mPrevButton.setOnClickListener(new PrevClickListener());
        mNextButton.setOnClickListener(new nextClickListener());
    }

    public void update() {
        updatePlayer();
        updateButton();
    }

    public void updatePlayer() {
        int visible = MusicController.isActive() ? View.VISIBLE : View.GONE;

        mFooterView.setVisibility(visible);
    }

    public void updateButton() {
        if (MusicController.isPlaying()) {
            mPlayButton.setVisibility(View.GONE);
            mPauseButton.setVisibility(View.VISIBLE);
        } else {
            mPlayButton.setVisibility(View.VISIBLE);
            mPauseButton.setVisibility(View.GONE);
        }
    }

    public void play() {
        MusicController.play();
        updateButton();
    }

    public void pause() {
        MusicController.pause();
        updateButton();
    }

    public void prev() {
        MusicController.prev();
    }

    public void next() {
        MusicController.next();
    }

    private class playClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            play();
        }
    }

    private class PauseClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            pause();
        }
    }

    private class PrevClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            prev();
        }
    }

    private class nextClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            next();
        }
    }
}
