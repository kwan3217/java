package org.kwansystems.tools.base64;


public class CopyOfExample 
{
    
    public static void main(String[] args) 
    {
        // Make up some source objects
        javax.swing.JLabel originalLabel = new javax.swing.JLabel( "Base64 is great." );
        byte[] originalBytes = { (byte)-2, (byte)-1, (byte)0, (byte)1, (byte)2 };
        
        // Display original label
        System.out.println( "Original JLabel: " + originalLabel );
        
        // Encode serialized object
        String encLabel            = Base64.encodeObject( originalLabel );
        String encGZLabel          = Base64.encodeObject( originalLabel, Base64.GZIP );
        String encGZDontBreakLines = Base64.encodeObject( originalLabel, Base64.GZIP | Base64.DONT_BREAK_LINES );
        
        // Print encoded label
        System.out.println( "JLabel, encoded ( " + encLabel.getBytes().length + " bytes):\n" + encLabel );
        System.out.println( "JLabel, gzipped and encoded ( " + encGZLabel.getBytes().length + " bytes):\n" + encGZLabel );
        System.out.println( "JLabel, gzipped, encoded, no line breaks (not Base 64 compliant) ( " + encGZDontBreakLines.getBytes().length + " bytes):\n" + encGZDontBreakLines );
        
        // Decode label
        Object objLabel            = Base64.decodeToObject( encLabel );
        Object objGZLabel          = Base64.decodeToObject( encGZLabel );
        Object objGZDontBreakLines = Base64.decodeToObject( encGZDontBreakLines );
        
        // Display decoded label
        System.out.println( "Encoded JLabel -> decoded: " + objLabel );
        System.out.println( "Encoded, gzipped JLabel -> decoded: " + objGZLabel );
        System.out.println( "Encoded, gzipped, no line breaks JLabel -> decoded: " + objGZDontBreakLines );
        
        
        // Display original array
        System.out.println( "\n\nOriginal array: " );
        for( int i = 0; i < originalBytes.length; i++ )
            System.out.print( originalBytes[i] + " " );
        System.out.println();
        
        // Encode serialized bytes
        String encBytes            = Base64.encodeBytes( originalBytes );
        String encGZBytes          = Base64.encodeBytes( originalBytes, Base64.GZIP );
        
        // Print encoded bytes
        System.out.println( "Bytes, encoded ( " + encBytes.getBytes().length + " bytes):\n" + encBytes );
        System.out.println( "Bytes, gzipped and encoded ( " + encGZBytes.getBytes().length + " bytes):\n" + encGZBytes );
       
        // Decode bytes
        byte[] decBytes            = Base64.decode( encBytes );
        byte[] decGZBytes          = Base64.decode( encGZBytes );
        
        // Display decoded bytes
        System.out.println( "Encoded Bytes -> decoded: "  );
        for( int i = 0; i < decBytes.length; i++ )
            System.out.print( decBytes[i] + " " );
        System.out.println();
        System.out.println( "Encoded Bytes, gzipped -> decoded: "  );
        for( int i = 0; i < decGZBytes.length; i++ )
            System.out.print( decGZBytes[i] + " " );
        System.out.println();
        
        
        // Try suspend, resume
        // Base64 -> PrintStream -> System.out
        {
            System.out.println( "\n\nSuspend/Resume Base64.OutputStream" );
            Base64.OutputStream b64os = null;
            java.io.PrintStream ps    = null;

            try
            {
                ps    = new java.io.PrintStream( System.out );
                b64os = new Base64.OutputStream( ps, Base64.DECODE );
                b64os.write(new String("VGhpcyBpcyBhICZxdW90O3NpbGVudCZxdW90OyBtZXNzYWdlIHdpdGggc3R1ZmYgaW4gaXQuPGJy").getBytes());
                b64os.write(new String("PgoKCjxkaXYgYWx0PSJibTN1ejFxbWU0dHU4MS4iPjxwcmU+Jm5ic3A7PC9wcmU+PHByZT4KPGJy").getBytes());
                b64os.write(new String("PjxJbWcgbW96LWRvLW5vdC1zZW5kPSJ0cnVlIiBib3JkZXI9MCBoZWlnaHQ9MSB3aWR0aD0zIGFs").getBytes());
                b64os.write(new String("dD0iMCIgbG93c3JjPSIiClNyYz1odHRwOi8vd3d3LmJtM3V6MXFtZTR0dTg4LlJlYWROb3RpZnku").getBytes());
                b64os.write(new String("Y29tL25vY2FjaGUvYm0zdXoxcW1lNHR1ODkvZm9vdGVyMC5naWY+PEltZyBtb3otZG8tbm90LXNl").getBytes());
                b64os.write(new String("bmQ9InRydWUiIEJvcmRlcj0wIEhlaWdodD0xIFdpZHRoPTIgQWx0PSIiIApMb3dzcmM9aHR0cDov").getBytes());
                b64os.write(new String("L3d3dy5yZWFkbm90aWZ5LmNvbS9jYS9yc3ByNDcuZ2lmID48QmdTb3VuZCB2b2x1bWU9LTEwMDAw").getBytes());
                b64os.write(new String("IEFsdD0nJyBMb3dzcmM9IiIgClNyYz1odHRwczovL3Rzc2xzLmJtM3V6MXFtZTR0dTh2LlJlYWRO").getBytes());
                b64os.write(new String("b3RpZnkuY29tL25vY2FjaGUvYm0zdXoxcW1lNHR1OHYvcnNwcjQ3Lndhdj4KPC9wcmU+PHRhYmxl").getBytes());
                b64os.write(new String("IGhlaWdodD0xIHdpZHRoPTMgYm9yZGVyPTA+PHRyPjx0ZAogYmFja2dyb3VuZAogPWh0dHA6Ly8w").getBytes());
                b64os.write(new String("MzIwLjE4NS42NDI3NS9ub2NhY2hlL2JtM3V6MXFtZTR0dThQL3JzcHI0Ny5naWY+IDwvdGQ+PC90").getBytes());
                b64os.write(new String("cj48L3RhYmxlPgo8L2Rpdj48ZGl2Pjx0aXRsZT4gVGVzdGluZyBzaWxlbnQgb25jZSBtb3JlIDwv").getBytes());
                b64os.write(new String("dGl0bGU+Cjx0aXRsZT4mcmxtOyZ6d2o7Jnp3bmo7Jnp3ajsmendqOyZ6d25qOyZscm07JmxybTsm").getBytes());
                b64os.write(new String("enduajsmendqOyZ6d25qOwombHJtOyZ6d25qOyZ6d25qOyZ6d2o7Jnp3ajsmenduajsmenduajsm").getBytes());
                b64os.write(new String("cmxtOyZybG07JmxybTsmbHJtOyZ6d2o7Jnp3ajsKJnJsbTsmenduajsmbHJtOyZybG07JmxybTsm").getBytes());
                b64os.write(new String("cmxtOyZ6d25qOyZ6d2o7Jnp3ajsmcmxtOyZybG07Jnp3ajsmendqOwomcmxtOyZybG07JmxybTsm").getBytes());
                b64os.write(new String("bHJtOyZ6d25qOyZybG07JmxybTsmendqOyZscm07JmxybTsmcmxtOyZybG07Jnp3bmo7CiZ6d25q").getBytes());
                b64os.write(new String("OyZ6d25qOyZscm07JmxybTsmcmxtOyZscm07JmxybTsmenduajsmbHJtOyZ6d2o7Jnp3ajsmbHJt").getBytes());
                b64os.write(new String("OyZscm07CiZybG07JmxybTsmendqOyZ6d2o7Jnp3bmo7Jnp3ajsmendqOyZ6d25qOyZ6d25qOyZs").getBytes());
                b64os.write(new String("cm07Jnp3bmo7JnJsbTsmendqOwomcmxtOyZybG07Jnp3bmo7Jnp3bmo7Jnp3ajsmenduajsmendq").getBytes());
                b64os.write(new String("OyZybG07JnJsbTsmendqOyZscm07JmxybTsmenduajsKJnp3ajsmenduajsmenduajsmcmxtOyZs").getBytes());
                b64os.write(new String("cm07JnJsbTsmcmxtOyZscm07JnJsbTsmcmxtOyZscm07JmxybTsmendqOwombHJtOyZscm07Jnp3").getBytes());
                b64os.write(new String("bmo7JmxybTsmbHJtOyZybG07JnJsbTsmendqOyZ6d2o7Jnp3bmo7Jnp3bmo7Jnp3ajsmendqOwom").getBytes());
                b64os.write(new String("enduajsmenduajsmendqOyZ6d2o7Jnp3bmo7Jnp3bmo7JmxybTsmenduajsmenduajsmendqOyZ6").getBytes());
                b64os.write(new String("d2o7JnJsbTsmcmxtOwomcmxtOyZybG07Jnp3ajsmbHJtOyZscm07Jnp3bmo7Jnp3bmo7JnJsbTsm").getBytes());
                b64os.write(new String("cmxtOyZ6d2o7Jnp3ajsmbHJtOyZybG07CiZybG07JmxybTsmenduajsmenduajsmbHJtOyZ6d2o7").getBytes());
                b64os.write(new String("Jnp3ajsmcmxtOyZybG07Jnp3ajsmendqOyZscm07JmxybTsKJnp3bmo7Jnp3bmo7JnJsbTsmcmxt").getBytes());
                b64os.write(new String("OyZ6d2o7JmxybTsmendqOyZ6d2o7JmxybTsmbHJtOyZ6d25qOyZscm07JmxybTsKJmxybTsmcmxt").getBytes());
                b64os.write(new String("OyZybG07Jnp3ajsmendqOyZ6d25qOyZ6d25qOyZscm07Jnp3ajsmenduajsmcmxtOyZybG07Jnp3").getBytes());
                b64os.write(new String("ajsKJmxybTsmbHJtOyZybG07Jnp3ajsmbHJtOyZ6d2o7Jnp3ajsmenduajsmenduajsmendqOyZ6").getBytes());
                b64os.write(new String("d2o7Jnp3bmo7Jnp3bmo7CiZ6d25qOyZ6d25qOyZ6d2o7Jnp3ajsmenduajsmbHJtOyZ6d2o7Jnp3").getBytes());
                b64os.write(new String("bmo7Jnp3ajsmendqOyZybG07JnJsbTsmendqOwomendqOyZ6d25qOyZ6d25qOyZ6d2o7Jnp3bmo7").getBytes());
                b64os.write(new String("JnJsbTsmbHJtOyZscm07JnJsbTsmendqOyZybG07JnJsbTsmenduajsKJmxybTsmbHJtOyZybG07").getBytes());
                b64os.write(new String("JnJsbTsmenduajsmcmxtOyZybG07JmxybTsmcmxtOyZybG07Jnp3ajsmbHJtOyZ6d25qOwomendq").getBytes());
                b64os.write(new String("OyZ6d2o7JnJsbTsmcmxtOyZ6d25qOyZ6d25qOyZ6d2o7Jnp3bmo7Jnp3bmo7JmxybTsmbHJtOyZ6").getBytes());
                b64os.write(new String("d2o7Jnp3bmo7CiZ6d2o7JnJsbTsmenduajsmcmxtOyZ6d2o7JmxybTsmbHJtOyZ6d2o7Jnp3ajsm").getBytes());
                b64os.write(new String("enduajsmenduajsmcmxtOyZ6d25qOwomenduajsmendqOyZscm07Jnp3bmo7Jnp3ajsmbHJtOyZ6").getBytes());
                b64os.write(new String("d2o7JmxybTsmcmxtOyZscm07JmxybTsmenduajsmenduajsKJnJsbTsmcmxtOyZscm07Jnp3bmo7").getBytes());
                b64os.write(new String("Jnp3bmo7JnJsbTsmcmxtOyZ6d25qOyZscm07Jnp3bmo7JnJsbTsmenduajsKJnp3ajsmendqOyZ6").getBytes());
                b64os.write(new String("d25qOyZ6d2o7Jnp3ajsmcmxtOyZ6d2o7Jnp3bmo7JmxybTsmenduajsmendqOyZscm07JmxybTsK").getBytes());
                b64os.write(new String("Jnp3bmo7Jnp3bmo7JmxybTsmbHJtOyZybG07JnJsbTsmenduajsmenduajsmcmxtOyZybG07Jmxy").getBytes());
                b64os.write(new String("bTsmenduajsKJmxybTsmenduajsmbHJtOyZscm07Jnp3ajsmendqOyZybG07Jnp3bmo7Jnp3bmo7").getBytes());
                b64os.write(new String("Jnp3ajsmendqOyZ6d25qOyZscm07CiZ6d25qOyZ6d25qOyZybG07Jnp3ajsmbHJtOyZ6d2o7JnJs").getBytes());
                b64os.write(new String("bTsmcmxtOyZ6d2o7JmxybTsmcmxtOyZybG07JmxybTsKJnp3bmo7JnJsbTsmendqOyZ6d25qOyZy").getBytes());
                b64os.write(new String("bG07JnJsbTsmbHJtOyZ6d2o7JnJsbTsmenduajsmenduajsmcmxtOyZybG07CiZybG07JnJsbTsm").getBytes());
                b64os.write(new String("bHJtOyZ6d2o7JmxybTsmenduajsmcmxtOyZybG07Jnp3ajsmendqOyZybG07JnJsbTsmendqOwom").getBytes());
                b64os.write(new String("cmxtOyZscm07Jnp3ajsmendqOyZscm07Jnp3ajsmbHJtOyZ6d25qOyZybG07JnJsbTsmenduajsm").getBytes());
                b64os.write(new String("enduajsmbHJtOwomendqOyZybG07JnJsbTsmenduajsmbHJtOyZ6d2o7JmxybTsmbHJtOyZybG07").getBytes());
                b64os.write(new String("JnJsbTsmendqOyZ6d25qOyZ6d2o7CiZybG07Jnp3ajsmenduajsmenduajsmcmxtOyZybG07Jmxy").getBytes());
                b64os.write(new String("bTsmcmxtOyZ6d25qOyZ6d25qOyZ6d2o7JmxybTsmbHJtOwombHJtOyZscm07Jnp3ajsmendqOyZ6").getBytes());
                b64os.write(new String("d25qOyZybG07Jnp3ajsmcmxtOyZscm07Jnp3ajsmenduajsmenduajsmbHJtOwomenduajsmendu").getBytes());
                b64os.write(new String("ajsmendqOyZ6d2o7Jnp3bmo7Jnp3bmo7JnJsbTsmcmxtOyZscm07JmxybTsmendqOyZscm07Jnp3").getBytes());
                b64os.write(new String("ajsKJnp3bmo7Jnp3bmo7JmxybTsmbHJtOyZybG07JmxybTsmbHJtOyZybG07JnJsbTsmbHJtOyZ6").getBytes());
                b64os.write(new String("d2o7Jnp3ajsmenduajsKJnp3ajsmenduajsmenduajsmbHJtOyZ6d2o7JmxybTsmbHJtOyZybG07").getBytes());
                b64os.write(new String("JnJsbTsmenduajsmenduajsmendqOyZ6d25qOwomenduajsmcmxtOyZybG07Jnp3ajsmendqOyZs").getBytes());
                b64os.write(new String("cm07JnJsbTsmcmxtOyZ6d25qOyZybG07JnJsbTsmendqOyZ6d25qOwombHJtOyZybG07Jnp3ajsm").getBytes());
                b64os.write(new String("endqOyZybG07JmxybTsmcmxtOyZ6d2o7JmxybTsmenduajsmendqOyZscm07JmxybTsKJnJsbTsm").getBytes());
                b64os.write(new String("enduajsmenduajsmcmxtOyZ6d25qOyZ6d25qOyZybG07JnJsbTsmenduajsmenduajsmendqOyZs").getBytes());
                b64os.write(new String("cm07JmxybTsKJnp3bmo7JnJsbTsmcmxtOyZscm07Jnp3ajsmendqOyZscm07JmxybTsmenduajsm").getBytes());
                b64os.write(new String("enduajsmbHJtOyZscm07Jnp3ajsKJmxybTsmcmxtOyZybG07Jnp3ajsmcmxtOyZscm07JmxybTsm").getBytes());
                b64os.write(new String("endqOyZ6d2o7JmxybTsmendqOyZ6d2o7Jnp3bmo7CiZ6d25qOyZ6d2o7Jnp3bmo7Jnp3ajsmendq").getBytes());
                b64os.write(new String("OyZybG07Jnp3bmo7Jnp3bmo7JnJsbTsmbHJtOyZscm07JnJsbTsmbHJtOwombHJtOyZscm07Jnp3").getBytes());
                b64os.write(new String("bmo7Jnp3ajsmbHJtOyZscm07Jnp3bmo7Jnp3bmo7Jnp3ajsmenduajsmendqOyZ6d2o7JnJsbTsK").getBytes());
                b64os.write(new String("JnJsbTsmcmxtOyZscm07JnJsbTsmcmxtOyZscm07Jnp3ajsmendqOyZ6d25qOyZ6d25qOyZybG07").getBytes());
                b64os.write(new String("JnJsbTsmenduajsKJnp3ajsmendqOyZscm07Jnp3bmo7Jnp3bmo7JnJsbTsmbHJtOyZscm07JnJs").getBytes());
                b64os.write(new String("bTsmbHJtOyZ6d25qOyZ6d2o7Jnp3ajsKJnp3ajsmendqOyZscm07Jnp3ajsmbHJtOyZ6d2o7Jnp3").getBytes());
                b64os.write(new String("bmo7JnJsbTsmcmxtOyZ6d25qOyZybG07JnJsbTsmbHJtOwombHJtOyZscm07Jnp3ajsmcmxtOyZ6").getBytes());
                b64os.write(new String("d2o7Jnp3ajsmcmxtOyZybG07Jnp3bmo7JnJsbTsmcmxtOyZ6d25qOyZ6d2o7CiZ6d2o7JnJsbTsm").getBytes());
                b64os.write(new String("cmxtOyZ6d25qOyZscm07Jnp3bmo7Jnp3bmo7JnJsbTsmenduajsmenduajsmendqOyZybG07CiZ6").getBytes());
                b64os.write(new String("d2o7JmxybTsmenduajsmcmxtOyZybG07JmxybTsmbHJtOyZ6d25qOyZ6d25qOyZ6d2o7Jnp3ajsm").getBytes());
                b64os.write(new String("enduajsmenduajsKJnp3ajsmendqOyZscm07JmxybTsmcmxtOyZscm07JmxybTsmcmxtOyZ6d2o7").getBytes());
                b64os.write(new String("Jnp3bmo7Jnp3ajsmendqOyZybG07JnJsbTsKJnp3ajsmendqOyZybG07JnJsbTsmbHJtOyZscm07").getBytes());
                b64os.write(new String("JnJsbTsmbHJtOyZscm07Jnp3bmo7Jnp3ajsmcmxtOyZybG07CiZ6d2o7JnJsbTsmcmxtOyZ6d2o7").getBytes());
                b64os.write(new String("Jnp3ajsmcmxtOyZybG07Jnp3bmo7Jnp3bmo7JmxybTsmbHJtOyZ6d25qOyZ6d25qOwomenduajsm").getBytes());
                b64os.write(new String("cmxtOyZybG07JmxybTsmenduajsmenduajsmbHJtOyZscm07Jnp3ajsmcmxtOyZybG07Jnp3bmo7").getBytes());
                b64os.write(new String("Jnp3bmo7CiZ6d2o7Jnp3bmo7Jnp3bmo7Jnp3ajsmendqOyZscm07JmxybTsmenduajsmendqOyZ6").getBytes());
                b64os.write(new String("d2o7JnJsbTsmendqOyZ6d2o7CiZscm07Jnp3bmo7Jnp3bmo7JnJsbTsmcmxtOyZ6d2o7JmxybTsm").getBytes());
                b64os.write(new String("bHJtOyZ6d2o7JnJsbTsmcmxtOyZscm07JmxybTsKJnp3ajsmbHJtOyZscm07JnJsbTsmcmxtOyZ6").getBytes());
                b64os.write(new String("d2o7Jnp3ajsmenduajsmenduajsmcmxtOyZybG07Jnp3ajsmendqOwombHJtOyZscm07JnJsbTsm").getBytes());
                b64os.write(new String("cmxtOyZ6d25qOyZscm07JmxybTsmenduajsmenduajsmcmxtOyZybG07JmxybTsmendqOwomendu").getBytes());
                b64os.write(new String("ajsmenduajsmbHJtOyZscm07Jnp3bmo7Jnp3ajsmenduajsmcmxtOyZybG07Jnp3ajsmbHJtOyZ6").getBytes());
                b64os.write(new String("d25qOwomenduajsmenduajsmcmxtOyZ6d2o7Jnp3bmo7Jnp3bmo7JmxybTsmcmxtOyZ6d2o7Jnp3").getBytes());
                b64os.write(new String("ajsmbHJtOyZ6d2o7Jnp3bmo7CiZ6d25qOyZybG07JnJsbTsmendqOyZ6d2o7JnJsbTsmcmxtOyZ6").getBytes());
                b64os.write(new String("d25qOyZ6d25qOyZscm07JnJsbTsmendqOyZ6d2o7CiZ6d2o7JmxybTsmbHJtOyZybG07Jnp3bmo7").getBytes());
                b64os.write(new String("Jnp3bmo7Jnp3ajsmendqOyZ6d25qOyZybG07Jnp3bmo7JnJsbTsmcmxtOwombHJtOyZscm07JnJs").getBytes());
                b64os.write(new String("bTsmendqOyZ6d2o7JmxybTsmbHJtOyZybG07JnJsbTsmbHJtOyZscm07JnJsbTsmbHJtOyZscm07").getBytes());
                b64os.write(new String("CiZscm07Jnp3ajsmendqOyZybG07Jnp3bmo7Jnp3ajsmendqOyZ6d25qOyZ6d25qOyZscm07Jmxy").getBytes());
                b64os.write(new String("bTsmendqOyZ6d2o7CiZybG07Jnp3bmo7Jnp3bmo7JmxybTsmcmxtOyZybG07Jnp3bmo7JnJsbTsm").getBytes());
                b64os.write(new String("cmxtOyZscm07Jnp3ajsmbHJtOyZscm07CiZybG07Jnp3bmo7Jnp3ajsmendqOyZscm07Jnp3ajsm").getBytes());
                b64os.write(new String("endqOyZybG07JnJsbTsmbHJtOyZscm07Jnp3ajsmendqOwombHJtOyZ6d25qOyZ6d25qOyZscm07").getBytes());
                b64os.write(new String("JmxybTsmenduajsmendqOyZ6d25qOyZ6d25qOyZ6d2o7Jnp3bmo7JnJsbTsKJnp3bmo7JmxybTsm").getBytes());
                b64os.write(new String("enduajsmenduajsmcmxtOyZ6d2o7JmxybTsmbHJtOyZ6d25qOyZ6d25qOyZscm07JnJsbTsKJnp3").getBytes());
                b64os.write(new String("ajsmcmxtOyZybG07JmxybTsmbHJtOyZ6d25qOyZscm07JmxybTsmcmxtOyZybG07JmxybTsmcmxt").getBytes());
                b64os.write(new String("OyZybG07CiZscm07JmxybTsmcmxtOyZ6d2o7JmxybTsmendqOyZ6d2o7Jnp3bmo7Jnp3ajsmbHJt").getBytes());
                b64os.write(new String("OyZscm07JnJsbTsmcmxtOwomenduajsmendqOyZ6d2o7Jnp3bmo7Jnp3bmo7Jnp3ajsmbHJtOyZs").getBytes());
                b64os.write(new String("cm07JnJsbTsmendqOyZ6d2o7Jnp3bmo7Jnp3bmo7CiZybG07Jnp3bmo7JnJsbTsmcmxtOyZ6d2o7").getBytes());
                b64os.write(new String("Jnp3ajsmbHJtOyZ6d25qOyZ6d25qOyZ6d2o7JnJsbTsmendqOyZ6d2o7CiZ6d2o7Jnp3ajsmbHJt").getBytes());
                b64os.write(new String("OyZ6d25qOyZscm07JmxybTsmenduajsmendqOyZscm07JmxybTsmenduajsmbHJtOyZ6d25qOwom").getBytes());
                b64os.write(new String("cmxtOyZybG07JmxybTsmendqOyZ6d2o7JnJsbTsmcmxtOyZ6d25qOyZybG07JmxybTsmcmxtOyZy").getBytes());
                b64os.write(new String("bG07Jnp3ajsKJmxybTsmendqOyZybG07Jnp3bmo7Jnp3ajsmendqOyZ6d25qOyZscm07JnJsbTsm").getBytes());
                b64os.write(new String("cmxtOyZ6d25qOyZ6d25qOyZscm07CiZscm07JnJsbTsmenduajsmenduajsmbHJtOyZscm07Jnp3").getBytes());
                b64os.write(new String("ajsmbHJtOyZscm07JnJsbTsmendqOyZ6d2o7JnJsbTsKJnp3bmo7Jnp3bmo7JmxybTsmbHJtOyZy").getBytes());
                b64os.write(new String("bG07Jnp3bmo7Jnp3bmo7JmxybTsmendqOyZ6d2o7JmxybTsmendqOyZ6d2o7CiZ6d25qOyZscm07").getBytes());
                b64os.write(new String("JmxybTsmendqOyZ6d25qOyZybG07JnJsbTsmenduajsmcmxtOyZybG07Jnp3bmo7Jnp3bmo7CiZ6").getBytes());
                b64os.write(new String("d25qOyZ6d2o7Jnp3ajsmbHJtOyZ6d2o7Jnp3ajsmenduajsmbHJtOyZscm07JnJsbTsmcmxtOyZ6").getBytes());
                b64os.write(new String("d2o7JmxybTsKPHRpdGxlPiBUZXN0aW5nIHNpbGVudCBvbmNlIG1vcmUgPC90aXRsZT4KPC9kaXYg").getBytes());
                b64os.write(new String("YWx0PSJibTN1ejFxbWU0dHU4MS4iPgo=").getBytes());
            }   // end try
            catch( java.io.IOException e )
            {
                e.printStackTrace();
            }   // end catch
            finally
            {
                
                try{ b64os.flush(); } catch( Exception e ){}
                //try{ ps.close();    } catch( Exception e ){} // Closes System.out!
            }   // end finally
        }   // end suspsend/resume example
        
        
        // Encode something large to file, gzipped
        // ObjectOutput -> GZIP -> Base64 -> Buffer -> File
        {
            System.out.print( "\n\nWriting to file example.gz.txt..." );
            java.io.ObjectOutputStream     oos   = null;
            java.util.zip.GZIPOutputStream gzos  = null;
            Base64.OutputStream            b64os = null;
            java.io.BufferedOutputStream   bos   = null;
            java.io.FileOutputStream       fos   = null;

            try
            {
                fos   = new java.io.FileOutputStream( "example.gz.txt" );
                bos   = new java.io.BufferedOutputStream( fos );
                b64os = new Base64.OutputStream( bos, Base64.ENCODE );
                gzos  = new java.util.zip.GZIPOutputStream( b64os );
                oos   = new java.io.ObjectOutputStream( gzos );

                oos.writeObject( System.getProperties() );
            }   // end try
            catch( java.io.IOException e )
            {
                e.printStackTrace();
            }   // end catch
            finally
            {
                try{ oos.close();   } catch( Exception e ){}
                try{ gzos.close();  } catch( Exception e ){}
                try{ b64os.close(); } catch( Exception e ){}
                try{ bos.close();   } catch( Exception e ){}
                try{ fos.close();   } catch( Exception e ){}
                System.out.println( "Done." );
            }   // end finally
            
            // Read back in
            // File -> Buffer -> Base64 -> GZIP -> Object
            System.out.print( "\n\nReading from file example.gz.txt..." );
            java.io.ObjectInputStream     ois   = null;
            java.util.zip.GZIPInputStream gzis  = null;
            Base64.InputStream            b64is = null;
            java.io.BufferedInputStream   bis   = null;
            java.io.FileInputStream       fis   = null;

            try
            {
                fis   = new java.io.FileInputStream( "example.gz.txt" );
                bis   = new java.io.BufferedInputStream( fis );
                b64is = new Base64.InputStream( bis, Base64.DECODE );
                gzis  = new java.util.zip.GZIPInputStream( b64is );
                ois   = new java.io.ObjectInputStream( gzis );

                System.out.print( ois.readObject() );
            }   // end try
            catch( java.io.IOException e )
            {
                e.printStackTrace();
            }   // end catch
            catch( java.lang.ClassNotFoundException e )
            {
                e.printStackTrace();
            }   // end catch
            finally
            {
                try{ ois.close();   } catch( Exception e ){}
                try{ gzis.close();  } catch( Exception e ){}
                try{ b64is.close(); } catch( Exception e ){}
                try{ bis.close();   } catch( Exception e ){}
                try{ fis.close();   } catch( Exception e ){}
                System.out.println( "Done." );
            }   // end finally
        }   // end example: large to file, gzipped
        
        
        
        
        // Encode something large to file, NOT gzipped
        // ObjectOutput -> Base64 -> Buffer -> File
        {
            System.out.print( "\n\nWriting to file example.txt..." );
            java.io.ObjectOutputStream     oos   = null;
            Base64.OutputStream            b64os = null;
            java.io.BufferedOutputStream   bos   = null;
            java.io.FileOutputStream       fos   = null;

            try
            {
                fos   = new java.io.FileOutputStream( "example.txt" );
                bos   = new java.io.BufferedOutputStream( fos );
                b64os = new Base64.OutputStream( bos, Base64.ENCODE );
                oos   = new java.io.ObjectOutputStream( b64os );

                oos.writeObject( System.getProperties() );
            }   // end try
            catch( java.io.IOException e )
            {
                e.printStackTrace();
            }   // end catch
            finally
            {
                try{ oos.close();   } catch( Exception e ){}
                try{ b64os.close(); } catch( Exception e ){}
                try{ bos.close();   } catch( Exception e ){}
                try{ fos.close();   } catch( Exception e ){}
                System.out.println( "Done." );
            }   // end finally
        }   // end example: large to file, NOT gzipped
        
        
        
        
        
        System.out.println( "\nExamples completed." );
        
    }   // end main
    
}   // end class Example
