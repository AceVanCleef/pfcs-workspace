package ch.fhnw.pfcs.opengl;

//  ------------  Vertex- und Fragment-Shaders mit Textur  -----------------------------------------

public class MyShaders {


    // -----------  Vertex-Shader (Pass-thru Shader) ------------------
    public static String vShader0 =

            "   #version 130                                     \n" +
                    "   in vec4 vertexPosition, vertexColor;             \n" +
                    "   out vec4 Color;                                  \n" +
                    "   void main()                                      \n" +
                    "   {  gl_Position = vertexPosition;                 \n" +
                    "      Color = vertexColor;                          \n" +
                    "   }";


    // -----------  Fragment-Shader (Pass-thru Shader) ------------------
    public static String fShader0 =

            "    #version 130                                   \n" +
                    "    in  vec4 Color;                                \n" +
                    "    out vec4 FragColor;                            \n" +
                    "    void main()                                    \n" +
                    "    {  FragColor = Color;                          \n" +
                    "    }";


    // -----------  Vertex-Shader mit Transformations-Matrizen  ------------------
    public static String vShader1 =

            "   #version 130                                         // Shader Language Version                 \n" +
                    "                                                                                                   \n" +
                    "   //  -------- Input/Output Variabeln  -----------                                                \n" +
                    "                                                                                                   \n" +
                    "   uniform mat4 modelViewMatrix, projMatrix;            // Transformations-Matrizen                \n" +
                    "   in vec4 vertexPosition, vertexColor, vertexNormal;   // Vertex-Attributes                       \n" +
                    "   out vec4 Color;                                      // Vertex-Farbe fuer Fragment-Shader       \n" +
                    "   void main()                                                                                     \n" +
                    "   {  vec4 vertex = modelViewMatrix * vertexPosition;    // ModelView=Transformation               \n" +
                    "      gl_Position = projMatrix * vertex;                 // Projection                             \n" +
                    "      Color = vertexColor;                                                                         \n" +
                    "   }";


    // -----------  Vertex-Shader mit Transformations-Matrizen und Beleuchtung  ------------------
    public static String vShader2 =

            "   #version 130                                         // Shader Language Version                 \n" +
                    "                                                                                                   \n" +
                    "   //  -------- Input/Output Variabeln  -----------                                                \n" +
                    "                                                                                                   \n" +
                    "   uniform mat4 modelViewMatrix, projMatrix;            // Transformations-Matrizen                \n" +
                    "   uniform vec4 lightPosition;                          // Position Lichtquelle (im Kam.System)    \n" +
                    "   uniform int shadingLevel;                            // 0 ohne Beleucht, 1 diffuse Refl.        \n" +
                    "   in vec4 vertexPosition, vertexColor, vertexNormal;   // Vertex-Attributes                       \n" +
                    "   out vec4 Color;                                      // Vertex-Farbe fuer Fragment-Shader       \n" +
                    "                                                                                                   \n" +
                    "   float ambient = 0.2, diffuse = 0.8;                  // Reflexions-Parameter                    \n" +
                    "                                                                                                   \n" +
                    "   void main()                                                                                     \n" +
                    "   {  vec4 vertex = modelViewMatrix * vertexPosition;    // ModelView=Transformation               \n" +
                    "      gl_Position = projMatrix * vertex;                 // Projection                             \n" +
                    "      Color = vertexColor;                                                                         \n" +
                    "      float Id;                                          // Helligkeit diffuse Reflexion           \n" +
                    "      if (shadingLevel >= 1)                                                                       \n" +
                    "      { vec3 normal = normalize((modelViewMatrix * vertexNormal).xyz);                             \n" +
                    "        vec3 toLight = normalize(lightPosition.xyz - vertex.xyz);                                  \n" +
                    "        Id = diffuse * dot(toLight, normal);             // Lambert                                \n" +
                    "        if ( Id < 0 ) Id = 0;                                                                      \n" +
                    "        vec3 whiteColor = vec3(1,1,1);                                                             \n" +
                    "        vec3 reflectedLight =  (ambient + Id) * Color.rgb;                                         \n" +
                    "        Color.rgb = min(reflectedLight, whiteColor);                                               \n" +
                    "      }                                                                                            \n" +
                    "   }";


    // -----------  Shaders mit Textur-Verarb.  ------------------

    public static String vShaderTx =

            "   #version 130                                         // Shader Language Version                 \n" +
                    "                                                                                                   \n" +
                    "   //  -------- Input/Output Variabeln  -----------                                                \n" +
                    "                                                                                                   \n" +
                    "   uniform mat4 modelViewMatrix, projMatrix;            // Transformations-Matrizen                \n" +
                    "   uniform vec4 lightPosition;                          // Position Lichtquelle (im Kam.System)    \n" +
                    "   uniform int shadingLevel;                            // 0 ohne Beleucht, 1 diffuse Refl., 2 Textur  \n" +
                    "   in vec4 vertexPosition, vertexColor, vertexNormal, vertexTexCoord;   // Vertex-Attributes       \n" +
                    "   out vec4 Color;                                      // Vertex-Farbe fuer Fragment-Shader       \n" +
                    "   out vec4 texCoord;                                   // Vertex-Farbe fuer Fragment-Shader       \n" +
                    "                                                                                                   \n" +
                    "   float ambient = 0.2, diffuse = 0.8;                  // Reflexions-Parameter                    \n" +
                    "                                                                                                   \n" +
                    "   void main()                                                                                     \n" +
                    "   {  vec4 vertex = modelViewMatrix * vertexPosition;    // ModelView=Transformation               \n" +
                    "      gl_Position = projMatrix * vertex;                 // Projection                             \n" +
                    "      Color = vertexColor;                                                                         \n" +
                    "      texCoord = vertexTexCoord;                         // Textur-Koord.                          \n" +
                    "      float Id;                                          // Helligkeit diffuse Reflexion           \n" +
                    "      if (shadingLevel >= 1)                                                                       \n" +
                    "      { vec3 normal = normalize((modelViewMatrix * vertexNormal).xyz);                             \n" +
                    "        vec3 toLight = normalize(lightPosition.xyz - vertex.xyz);                                  \n" +
                    "        Id = diffuse * dot(toLight, normal);             // Lambert                                \n" +
                    "        if ( Id < 0 ) Id = 0;                                                                      \n" +
                    "        vec3 whiteColor = vec3(1,1,1);                                                             \n" +
                    "        vec3 reflectedLight =  (ambient + Id) * Color.rgb;                                         \n" +
                    "        Color.rgb = min(reflectedLight, whiteColor);                                               \n" +
                    "      }                                                                                            \n" +
                    "   }";


    // -----------  Fragment-Shader mit Textur ------------------
    public static String fShaderTx =

            "    #version 130                                   \n" +
                    "    in  vec4 Color, texCoord;                      \n" +
                    "    uniform int shadingLevel;                      \n" +
                    "    uniform sampler2D myTexture;                   \n" +
                    "    out vec4 FragColor;                            \n" +
                    "    void main()                                    \n" +
                    "    {  if ( shadingLevel == 2 )                       \n" +
                    "        FragColor = min(Color * texture(myTexture, texCoord.xy).rgba, vec4(1.0)); \n" +
                    "      else                                            \n" +
                    "        FragColor = Color;                           \n" +
                    "    }";

}
