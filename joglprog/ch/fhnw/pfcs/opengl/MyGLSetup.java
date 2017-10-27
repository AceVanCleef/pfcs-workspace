package ch.fhnw.pfcs.opengl;//  ---------  Compile/Link Shader-Programs  -------------------------

import com.jogamp.opengl.*;

public class MyGLSetup {

    public static int setupProgram(GL3 gl,
                                   String vShader,   // Vertex-Shader
                                   String fShader)   // Fragment-Shader
    {
        int vShaderId = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
        int fShaderId = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);


        gl.glShaderSource(vShaderId, 1, new String[]{vShader}, null);
        gl.glCompileShader(vShaderId);                                      // Compile Vertex Shader
        System.out.println("VertexShaderLog:");
        System.out.println(getShaderInfoLog(gl, vShaderId));
        System.out.println();


        gl.glShaderSource(fShaderId, 1, new String[]{fShader}, null);
        gl.glCompileShader(fShaderId);                                     // Compile Fragment Shader
        System.out.println("FragmentShaderLog:");
        System.out.println(getShaderInfoLog(gl, fShaderId));
        System.out.println();

        int programId = gl.glCreateProgram();
        gl.glAttachShader(programId, vShaderId);
        gl.glAttachShader(programId, fShaderId);
        gl.glLinkProgram(programId);                                       // Link Program
        gl.glUseProgram(programId);                                        // Activate Programmable Pipeline
        System.out.println("ProgramInfoLog:");
        System.out.println(getProgramInfoLog(gl, programId));
        System.out.println();
        return programId;
    }


    public static String getProgramInfoLog(GL3 gl, int obj)               // Info- and Error-Messages
    {
        int params[] = new int[1];
        gl.glGetProgramiv(obj, GL3.GL_INFO_LOG_LENGTH, params, 0);       // get log-length
        int logLen = params[0];
        if (logLen <= 0)
            return "";
        byte[] bytes = new byte[logLen + 1];
        int[] retLength = new int[1];
        gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);     // get log-data
        String logMessage = new String(bytes);
        int iend = logMessage.indexOf(0);
        if (iend < 0) iend = 0;
        return logMessage.substring(0, iend);
    }


    static public String getShaderInfoLog(GL3 gl, int obj)               // Info- and Error-Messages
    {
        int params[] = new int[1];
        gl.glGetShaderiv(obj, GL3.GL_INFO_LOG_LENGTH, params, 0);         // get log-length
        int logLen = params[0];
        if (logLen <= 0)
            return "";
        // Get the log
        byte[] bytes = new byte[logLen + 1];
        int[] retLength = new int[1];
        gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);       // get log-data
        String logMessage = new String(bytes);
        int iend = logMessage.indexOf(0);
        if (iend < 0) iend = 0;
        return logMessage.substring(0, iend);
    }

}
