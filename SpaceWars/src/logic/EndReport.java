package logic;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import clientServer.Client;

public class EndReport implements Report {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4004984865885813172L;
	public List<Report> roundReports; 
	public Client winner[];
	public Client looser[];
	public EndReport(){
		this.roundReports = new LinkedList<Report>();
	}
	public void addWinner(Client winner){
		this.winner = new Client[1];
		this.winner[0]=winner;
	}
	public void addWinner(Client winner, Client otherWinner){
		this.winner = new Client[2];
		this.winner[0]=winner;
		this.winner[1]=otherWinner;
	}
	public void addLooser(Client looser){
		this.looser = new Client[1];
		this.looser[0]=looser;
	}
	public void addLooser(Client looser, Client otherLooser){
		this.looser = new Client[2];
		this.looser[0]=looser;
		this.looser[1]=otherLooser;
	}
	private void addRoundReport(Report roundReport){
		this.roundReports.add(roundReport);
	}
	@Override
	public void addReport(Report report) {
		this.addRoundReport((RoundReport)report);
	}
	
	public JSONObject endReportToJSON(){
		JSONObject report = new JSONObject();
		try {
			report.put("winner", this.winner[0].getUsername());
		} catch (JSONException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			report.put("looser", this.looser[0].getUsername());
		} catch (JSONException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		report.put("roundReports", this.roundReports);
		report.put("roundCount", this.roundReports.size());
		
		
		return report;
	}
	@Override
	public String toString() {
		String s="";
		for(Client p:this.winner){
			s+=p.toString()+" ";
		}
		s+="\n";
		for(Client p:this.looser){
			s+=p.toString()+" ";
		}
		for(Report r:this.roundReports){
			s+=r.toString()+"\n";
		}
		return s;
	}
}
