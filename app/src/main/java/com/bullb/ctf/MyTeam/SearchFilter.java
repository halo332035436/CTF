package com.bullb.ctf.MyTeam;

import android.widget.Filter;
import com.bullb.ctf.Model.Score;
import java.util.ArrayList;

public class SearchFilter extends Filter {
        private Score[] score;
        private ArrayList<Score> filteredScore;
        private MyTeamRecyclerViewAdapter adapter;

        public SearchFilter(Score[] s, MyTeamRecyclerViewAdapter adapter) {
            this.adapter = adapter;
            this.score = s;
            this.filteredScore = new ArrayList();
        }

        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            filteredScore.clear();
            final FilterResults results = new FilterResults();

            for (final Score s : score) {
                if (s.getUserName().trim().contains(constraint)) {
                    filteredScore.add(s);
                }
            }

            results.values = filteredScore;
            results.count = filteredScore.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Score[] a = new Score[filteredScore.size()];
            adapter.setScore(filteredScore.toArray(a));
            adapter.notifyDataSetChanged();
        }
}
