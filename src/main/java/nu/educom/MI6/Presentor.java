package nu.educom.MI6;

import javax.swing.*;

class Presentor extends Thread implements IPresentor {
    IView theView;
    AgentModel model;
    boolean running = true;

    Presentor(IView view, AgentModel model){
        theView = view;
        view.addPresentorListener(this);
        this.model = model;
    }
    public AgentModel getModel(){
        return model;
    }
    public IView getTheView(){
        return theView;
    }

    public void run(){
        while(running){
//            System.out.println("run() thread name: " + Thread.currentThread().getName());
            try {
                theView.triggerAskLogin();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("After asklogin Loop");
        }
        theView.close();
    }

    public void handleLogin() {
        String serviceNumber = theView.getServiceNumber();
//        model.printAgent(Integer.parseInt(serviceNumber));

        if(!model.validNumber(serviceNumber)){
            theView.showMessage("Enemy");
        }else{
            Agent agent = model.validateServiceNumber(serviceNumber);
            if(agent==null){
                theView.showMessage("ACCESS DENIED");
            }else{
//                System.out.println("Seconds since last login:" + model.authenticateAgent());
                if(model.authenticateAgent()>=0) { //Check for timout
                    // In deze IF gaat nog iets fout
                    handlePassword();
                }else{
                    theView.showMessage("YOU ARE TIMED OUT! \nSeconds left: " + model.authenticateAgent());
                }
            }
        }
    }

    public void handlePassword() {
        String passPhrase = theView.getPassPhrase();
//        model.printAgentsList();
        if(!model.validatePass(passPhrase, model.getCurrentAgent())){
//            model.addBlackList(model.getCurrentAgent().getServiceNumber());
            theView.showMessage("TIME OUT");
            model.storeLoginAttempt(new LoginAttempt(model.getCurrentAgent().getServiceNumber(), false));
        }else{
            model.storeLoginAttempt(new LoginAttempt(model.getCurrentAgent().getServiceNumber(), false));
            theView.showMessage("Welcome agent " + model.getCurrentAgent().getFormattedServiceNumber());
        }
    }
}
