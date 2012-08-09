/*
THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. NEITHER RECIPIENT NOR
ANY CONTRIBUTORS SHALL HAVE ANY LIABILITY FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING WITHOUT
LIMITATION LOST PROFITS), HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THE PROGRAM
OR THE EXERCISE OF ANY RIGHTS GRANTED HEREUNDER, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGES.

The name of the Copyright Holder may not be used to endorse or promote
products derived from this software without specific prior written permission.

Copyright 2000 George Rhoten and others.

*/

package com.centralnexus.test;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import com.centralnexus.input.*;

public class WindowTest extends Frame implements Runnable, JoystickListener {

    Joystick joy;

    /** polling interval for this joystick */
    private int interval = 50;

    Thread thread = new Thread(this);

    Label buttonLabel = new Label(),
          button2Label = new Label(),
          deadZoneLabel = new Label(),
          xLabel = new Label(),
          yLabel = new Label(),
          zLabel = new Label(),
          rLabel = new Label(),
          uLabel = new Label(),
          vLabel = new Label(),
          povLabel = new Label();
    Label intervalLabel = new Label();

    WindowTest() throws IOException {
        super();

        joy = Joystick.createInstance();
        doWindowLayout();
    }

    WindowTest(int joystickID) throws IOException {
        super();

        joy = Joystick.createInstance(joystickID);
        doWindowLayout();
    }

    private void doWindowLayout() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
        setTitle("Joystick Test");

        setLayout(new GridLayout(20, 2));
        add(new Label("Number Of Devices: ", Label.RIGHT));
        add(new Label(Integer.toString(Joystick.getNumDevices())));
        add(new Label("Joystick ID: ", Label.RIGHT));
        add(new Label("joy(" + Integer.toString(joy.getID()) + ")"));
        add(new Label("Description joy#1: ", Label.RIGHT));
        add(new Label(joy.toString()));
        add(new Label("Capabilities:", Label.RIGHT));
        add(new Label("joy(0x" + Integer.toHexString(joy.getCapabilities()) + ")"));
        add(new Label("Axes: ", Label.RIGHT));
        add(new Label("joy(" + Integer.toString(joy.getNumAxes()) + ")"));
        add(new Label("Buttons: ", Label.RIGHT));
        add(new Label("joy(" + Integer.toString(joy.getNumButtons()) + ")"));
        add(new Label("Dead Zone Size: ", Label.RIGHT));
        add(deadZoneLabel);
        add(new Label("Buttons Pressed: 0x", Label.RIGHT));
        add(buttonLabel);
        add(new Label("X: ", Label.RIGHT));
        add(xLabel);
        add(new Label("Y: ", Label.RIGHT));
        add(yLabel);
        add(new Label("Z: ", Label.RIGHT));
        add(zLabel);
        add(new Label("R: ", Label.RIGHT));
        add(rLabel);
        add(new Label("U: ", Label.RIGHT));
        add(uLabel);
        add(new Label("V: ", Label.RIGHT));
        add(vLabel);
        add(new Label("POV: ", Label.RIGHT));
        add(povLabel);
        add(new Label("Polling interval: ", Label.RIGHT));
        add(intervalLabel);
    }

    /**
     * This is used by the internal thread.  It creates a lot of String
     * objects, so it uses the garbage collector a lot.  Since this is
     * for testing only, this is not a problem for speed.
     */
    public void run() {
        for (;;) {
            joy.poll();

            updateFieldsEx(joy);
            try {
                Thread.sleep(interval);
            } catch(InterruptedException e) {
                break;
            }
        }
    }

    public void joystickAxisChanged(Joystick j) {
//        System.out.println(j.toString());
        if (j == joy) {
            updateFieldsEx(j);
        }
        else {
            updateFields(j);
        }
    }

    public void joystickButtonChanged(Joystick j) {
//        System.out.println(j.toString());
        if (j == joy) {
            updateFieldsEx(j);
        }
        else {
            updateFields(j);
        }
    }

    public void setPollInterval(int pollMillis) {
        interval = pollMillis;
        joy.setPollInterval(pollMillis);
        intervalLabel.setText(Integer.toString(interval));
    }

    public void updateFields(Joystick joystick) {
        button2Label.setText(Integer.toHexString(joystick.getButtons()));
    }

    public void updateFieldsEx(Joystick joystick) {
        buttonLabel.setText(String.format("%04X", joystick.getButtons()));
        xLabel.setText(Double.toString(joystick.getX()));
        yLabel.setText(Double.toString(joystick.getY()));
        zLabel.setText(Double.toString(joystick.getZ()));
        rLabel.setText(Double.toString(joystick.getR()));
        uLabel.setText(Double.toString(joystick.getU()));
        vLabel.setText(Double.toString(joystick.getV()));
        povLabel.setText(Double.toString(joystick.getPOV()));
    }

    public void startPolling() {
        thread.start();
    }

    public void addListeners() {
        joy.addJoystickListener(this);
    }

    public void setDeadZone(double deadZone) {
        joy.setDeadZone(deadZone);
        updateDeadZone();
    }

    public void updateDeadZone() {
        deadZoneLabel.setText("joy(" + joy.getDeadZone() + ")");
    }

    private static void help() {
        System.out.println("Help:");
        System.out.println(" -h This help screen info");
        System.out.println(" -v Verbose Joystick debug information");
        System.out.println(" -j:n Set the Joystick ID to test (n is an integer)");
        System.out.println(" -j2:n Set the second joystick ID to test (n is an integer)");
        System.out.println(" -d:n Set the dead zone size of the Joystick (n is a real number)");
        System.out.println(" -d2:n Set the dead zone size of the second Joystick (n is a real number)");
    }

    public static void main(String args[]) {
        try {
            WindowTest mainFrame;
            int joystickNum = -1;
            double deadZone = -1.0;
            int interval = 10;

            for (int idx = 0; idx < args.length; idx++) {
                if (args[idx].startsWith("-d:")) {
                    deadZone = 
                        Double.valueOf(args[idx].substring(3, args[idx].length()))
                        .doubleValue();
                }
                else if (args[idx].startsWith("-i:")) {
                    interval = 
                        Integer.valueOf(args[idx].substring(3, args[idx].length()))
                        .intValue();
                }
                else if (args[idx].startsWith("-j:")) {
                    joystickNum = 
                        Integer.valueOf(args[idx].substring(3, args[idx].length()))
                        .intValue();
                }
                else if (args[idx].startsWith("-v")) {
                    for (int id = -1; id <= Joystick.getNumDevices(); id++) {
                        System.out.println("Joystick " + id + ": " + Joystick.isPluggedIn(id));
                    }
                }
                else if (args[idx].startsWith("-h")) {
                    help();
                }
                else {
                    System.out.println("Unknown option: " + args[idx]);
                    help();
                }
            }
            if (joystickNum >= 0) {
                mainFrame = new WindowTest(joystickNum);
            }
            else {
                mainFrame = new WindowTest();
            }
            if (deadZone >= 0.0) {
                mainFrame.setDeadZone(deadZone);
            }
            mainFrame.setPollInterval(interval);
            mainFrame.updateDeadZone();
            mainFrame.pack();
            mainFrame.setTitle("Polling Joystick");
            //mainFrame.show();
            mainFrame.setVisible(true);
            mainFrame.startPolling();

        } catch (IOException e) {
            System.err.println("");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
