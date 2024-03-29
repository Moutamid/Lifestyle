package com.lifestyle.machines;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.lifestyle.DAO.DAOMachine;
import com.lifestyle.DAO.DAOProfil;
import com.lifestyle.DAO.DAORecord;
import com.lifestyle.DAO.IRecord;
import com.lifestyle.DAO.Machine;
import com.lifestyle.DAO.Profile;
import com.lifestyle.MainActivity;
import com.lifestyle.R;
import com.lifestyle.fonte.FonteHistoryFragment;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.List;

public class ExerciseDetailsPager extends Fragment {




    Toolbar top_toolbar = null;
    long machineIdArg = 0;
    long machineProfilIdArg = 0;
    FragmentPagerItemAdapter pagerAdapter = null;
    ViewPager mViewPager = null;
    SmartTabLayout viewPagerTab = null;
    ImageButton machineDelete = null;
    ImageButton machineSave = null;
    MaterialFavoriteButton machineFavorite = null;
    Machine machine = null;
    boolean isFavorite = false;
    boolean toBeSaved = false;
    DAOMachine mDbMachine = null;
    DAORecord mDbRecord = null;
    private String name;
    private int id;
    private View.OnClickListener onClickToolbarItem = v -> {
        // Handle presses on the action bar items
        switch (v.getId()) {
            case R.id.action_machine_save:
                saveMachine();
                getActivity().findViewById(R.id.tab_machine_details).requestFocus();
                break;
            case R.id.action_machine_delete:
                deleteMachine();
                break;
            default:
                saveMachineDialog();
        }
    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static ExerciseDetailsPager newInstance(long machineId, long machineProfile) {
        ExerciseDetailsPager f = new ExerciseDetailsPager();

        // Supply index input as an argument.

        Bundle args = new Bundle();
        args.putLong("machineID", machineId);
        args.putLong("machineProfile", machineProfile);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.exercise_pager, container, false);

        // Locate the viewpager in activity_main.xml
        mViewPager = view.findViewById(R.id.pager);

        if (mViewPager.getAdapter() == null) {

            //getting valuie from bundle
            Bundle args = this.getArguments();
            machineIdArg = args.getLong("machineID");
            machineProfilIdArg = args.getLong("machineProfile");

            pagerAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(this.getContext())
                .add("Exercise", MachineDetailsFragment.class, args)
                .add("History", FonteHistoryFragment.class, args)
                .create());


            //setting adapter
            mViewPager.setAdapter(pagerAdapter);

            viewPagerTab = view.findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(mViewPager);

            viewPagerTab.setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    Fragment frag1 = pagerAdapter.getPage(position);
                    if (frag1 != null)
                        frag1.onHiddenChanged(false); // Refresh data
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        mDbRecord = new DAORecord(getContext());
        mDbMachine = new DAOMachine(getContext());

        ((MainActivity) getActivity()).getActivityToolbar().setVisibility(View.GONE);
        top_toolbar = view.findViewById(R.id.actionToolbarMachine);
        top_toolbar.setNavigationIcon(R.drawable.ic_back);
        top_toolbar.setNavigationOnClickListener(onClickToolbarItem);

        machineDelete = view.findViewById(R.id.action_machine_delete);
        machineSave = view.findViewById(R.id.action_machine_save);
        machineFavorite = view.findViewById(R.id.favButton);
        machineFavorite.setOnClickListener(v -> {
            MaterialFavoriteButton mFav = (MaterialFavoriteButton) v;
            boolean t = mFav.isFavorite();
            mFav.setFavoriteAnimated(!t);
            isFavorite = !t;
            requestForSave();
        });
        machine = mDbMachine.getMachine(machineIdArg);
        // TODO gérer quand il n'y  a pas de machine existante.
        machineFavorite.setFavorite(machine.getFavorite());

        machineSave.setOnClickListener(onClickToolbarItem);
        machineSave.setVisibility(View.GONE);
        // Hide Save button by default

        machineDelete.setOnClickListener(onClickToolbarItem);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void requestForSave() {
        toBeSaved = true; // setting state
        machineSave.setVisibility(View.VISIBLE);
    }

    private void saveMachineDialog() {
        if (getExerciseFragment().toBeSaved() || toBeSaved) {

            // Display a dialog box to confirm
            AlertDialog.Builder backDialogBuilder = new AlertDialog.Builder(getActivity());

            backDialogBuilder.setTitle(getActivity().getResources().getText(R.string.global_confirm));
            backDialogBuilder.setMessage(getActivity().getResources().getText(R.string.backDialog_confirm_text));


            // If so, delete the database and redo a Start.

            backDialogBuilder.setPositiveButton(getResources().getString(R.string.global_yes), (dialog, which) -> {
                if (saveMachine()) {
                    getActivity().onBackPressed();
                }
            });

            backDialogBuilder.setNegativeButton(getResources().getString(R.string.global_no), (dialog, which) -> getActivity().onBackPressed());

            AlertDialog backDialog = backDialogBuilder.create();
            backDialog.show();

        } else {
            getActivity().onBackPressed();
        }
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    private boolean saveMachine() {
        boolean result = false;
        final Machine initialMachine = machine;
        final Machine newMachine = getExerciseFragment().getMachine();


        final String lMachineName = newMachine.getName(); // Potential new name in the EditText



        // If the name is different from the current name
        if (lMachineName.equals("")) {
            KToast.warningToast(getActivity(), getResources().getText(R.string.name_is_required).toString(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
        } else if (!initialMachine.getName().equals(lMachineName)) {
            final Machine machineWithSameName = mDbMachine.getMachine(lMachineName);



            // If a machine exists with the same name = Merge
            if (machineWithSameName != null && newMachine.getId() != machineWithSameName.getId() && newMachine.getType() != machineWithSameName.getType()) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());

                dialogBuilder.setTitle(getActivity().getResources().getText(R.string.global_warning));
                dialogBuilder.setMessage(R.string.renameMachine_error_text2);
                dialogBuilder.setPositiveButton(getResources().getText(R.string.global_yes), (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            } else if (machineWithSameName != null && newMachine.getId() != machineWithSameName.getId() && newMachine.getType() == machineWithSameName.getType()) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());

                dialogBuilder.setTitle(getActivity().getResources().getText(R.string.global_warning));
                dialogBuilder.setMessage(getActivity().getResources().getText(R.string.renameMachine_warning_text));
                // If so, delete the database and redo a Start.
                dialogBuilder.setPositiveButton(getResources().getText(R.string.global_yes), (dialog, which) -> {
                    // Rename all the records with that machine and rename them
                    DAORecord lDbRecord = new DAORecord(getView().getContext());
                    DAOProfil mDbProfil = new DAOProfil(getView().getContext());
                    Profile lProfile = mDbProfil.getProfil(machineProfilIdArg);

                    List<IRecord> listRecords = lDbRecord.getAllRecordByMachinesArray(lProfile, initialMachine.getName()); // Recupere tous les records de la machine courante
                    for (IRecord record : listRecords) {
                        record.setExercise(newMachine.getName());
                        // Change with the new name. Normally not useful.
                        record.setExerciseKey(machineWithSameName.getId()); // Puts the ID of the new machine


                        lDbRecord.updateRecord(record);
                        // Update
                    }

                    mDbMachine.delete(initialMachine);
                    // Delete the old machine

                    toBeSaved = false;
                    machineSave.setVisibility(View.GONE);
                    getActivity().onBackPressed();
                });

                dialogBuilder.setNegativeButton(getResources().getText(R.string.global_no), (dialog, which) -> {
                    // Do nothing but close the dialog
                    dialog.dismiss();
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            } else {
                newMachine.setFavorite(machineFavorite.isFavorite());
                this.mDbMachine.updateMachine(newMachine);

                // Rename all the records with that machine and rename them
                DAORecord lDbRecord = new DAORecord(getContext());
                DAOProfil mDbProfil = new DAOProfil(getContext());
                Profile lProfile = mDbProfil.getProfil(machineProfilIdArg);
                List<IRecord> listRecords = lDbRecord.getAllRecordByMachinesArray(lProfile, initialMachine.getName()); // Recupere tous les records de la machine courante
                for (IRecord record : listRecords) {
                    record.setExercise(lMachineName); // Change with the new name
                    lDbRecord.updateRecord(record); // update
                }

                machineSave.setVisibility(View.GONE);
                toBeSaved = false;
                getExerciseFragment().machineSaved();
                result = true;
            }
        } else {


            // If the name has not been changed.
            newMachine.setFavorite(machineFavorite.isFavorite());
            mDbMachine.updateMachine(newMachine);

            machineSave.setVisibility(View.GONE);
            toBeSaved = false;
            getExerciseFragment().machineSaved();
            result = true;
        }
        return result;
    }

    private void deleteMachine() {
        // display an alert message
        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(this.getActivity());

        deleteDialogBuilder.setTitle(getActivity().getResources().getText(R.string.global_confirm));
        deleteDialogBuilder.setMessage(getActivity().getResources().getText(R.string.deleteMachine_confirm_text));

        // If so, delete the database and redo a Start.
        deleteDialogBuilder.setPositiveButton(this.getResources().getString(R.string.global_yes), (dialog, which) -> {
            // Suppress the machine
            mDbMachine.delete(machine);
            // Suppress the associated Fontes records
            deleteRecordsAssociatedToMachine();
            getActivity().onBackPressed();
        });

        deleteDialogBuilder.setNegativeButton(this.getResources().getString(R.string.global_no), (dialog, which) -> {
            // Do nothing
            dialog.dismiss();
        });

        AlertDialog deleteDialog = deleteDialogBuilder.create();
        deleteDialog.show();
    }

    private void deleteRecordsAssociatedToMachine() {
        DAORecord mDbRecord = new DAORecord(getContext());
        DAOProfil mDbProfil = new DAOProfil(getContext());

        Profile lProfile = mDbProfil.getProfil(this.machineProfilIdArg);

        List<IRecord> listRecords = mDbRecord.getAllRecordByMachinesArray(lProfile, machine.getName());
        for (IRecord record : listRecords) {
            mDbRecord.deleteRecord(record.getId());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.machine_details_menu, menu);

        MenuItem item = menu.findItem(R.id.action_machine_save);
        item.setVisible(toBeSaved);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public MachineDetailsFragment getExerciseFragment() {
        MachineDetailsFragment mpExerciseFrag;
        mpExerciseFrag = (MachineDetailsFragment) pagerAdapter.getPage(0);
        return mpExerciseFrag;
    }

    public FonteHistoryFragment getHistoricFragment() {
        FonteHistoryFragment mpHistoryFrag;
        mpHistoryFrag = (FonteHistoryFragment) pagerAdapter.getPage(1);
        return mpHistoryFrag;
    }

    public ViewPager getViewPager() {
        return (ViewPager) getView().findViewById(R.id.pager);
    }

    public FragmentPagerItemAdapter getViewPagerAdapter() {
        return (FragmentPagerItemAdapter) ((ViewPager) (getView().findViewById(R.id.pager))).getAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {


        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            // refresh the current fragment

            if (getViewPagerAdapter() != null) {
                // Way to refresh all the fragments. Be careful, the Views of the fragments may have been destroyed.


                // This must therefore be taken into account in the refresh of the fragments.
                Fragment frag1;
                for (int i = 0; i < 3; i++) {
                    frag1 = getViewPagerAdapter().getPage(i);
                    if (frag1 != null)
                        frag1.onHiddenChanged(false); // Refresh data
                }
            }
        }
    }
}
