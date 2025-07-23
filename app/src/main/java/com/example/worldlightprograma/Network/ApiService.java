package com.example.worldlightprograma.Network;

import com.example.worldlightprograma.Models.Auth.ReqAuth;
import com.example.worldlightprograma.Models.Auth.RptaAuth;
import com.example.worldlightprograma.Models.Auth.RptaComprobacionMembresia;
import com.example.worldlightprograma.Models.User.ReqRegistro;
import com.example.worldlightprograma.Models.User.RptaClasesInscritas;
import com.example.worldlightprograma.Models.User.RptaDatosPrincipal;
import com.example.worldlightprograma.Models.User.RptaPlanNutricional;
import com.example.worldlightprograma.Models.User.RptaRegistro;
import com.example.worldlightprograma.Models.User.RptaClasesDisponibles;
import com.example.worldlightprograma.Models.User.RptaRutinaActiva;
import com.example.worldlightprograma.Models.User.ReqCrearRutina;
import com.example.worldlightprograma.Models.User.RptaEjercicios;
import com.example.worldlightprograma.Models.User.RptaDetalleRutina;
import com.example.worldlightprograma.Models.User.RptaPerfilCliente;
import com.example.worldlightprograma.Models.User.ReqEditarPerfil;
import com.example.worldlightprograma.Models.User.ReqAgregarEjercicio;
import com.example.worldlightprograma.Models.User.ReqInscribirseClase;
import com.example.worldlightprograma.Models.User.ReqEliminarEjercicio;
import com.example.worldlightprograma.Models.User.ReqRecuperarCorreo;
import com.example.worldlightprograma.Models.User.ReqRecuperarCodigo;
import com.example.worldlightprograma.Models.User.ReqRecuperarNuevaContrasena;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.HTTP;

public interface ApiService {
    @POST("auth")
    Call<RptaAuth> obtenerToken(@Body ReqAuth reqAuth);

    @POST("api/registrarcliente")
    Call<RptaRegistro> registrarCliente(@Body ReqRegistro reqRegistro);

    @GET("api/membresia/estado")
    Call<RptaDatosPrincipal> obtenerDataPrincipal(@Header("Authorization") String authorization);

    @GET("api/clases_inscritas")
    Call<RptaClasesInscritas> obtenerClasesInscritas(@Header("Authorization") String authorization);

    @GET("api/clases_disponibles")
    Call<RptaClasesDisponibles> obtenerClasesDisponibles(@Header("Authorization") String authorization);

    @GET("api/membresia/activo")
    Call<RptaComprobacionMembresia> obtenerComprobacion(@Header("Authorization") String authorization);

    @GET("api/plan_nutricional")
    Call<RptaPlanNutricional> obtenerPlanNutricional(@Header("Authorization") String authorization);

    @GET("api/rutinas/activa")
    Call<RptaRutinaActiva> obtenerRutinaActiva(@Header("Authorization") String authorization);

    @POST("api/rutinas/crear")
    Call<RptaRegistro> crearRutina(@Header("Authorization") String authorization, @Body ReqCrearRutina req);

    @POST("api/rutinas/agregar_ejercicio")
    Call<RptaRegistro> agregarEjercicioARutina(@Header("Authorization") String authorization, @Body ReqAgregarEjercicio req);

    @POST("api/inscribirse_clase")
    Call<RptaRegistro> inscribirseClase(@Header("Authorization") String authorization, @Body ReqInscribirseClase req);

    @GET("api/ejercicios")
    Call<RptaEjercicios> obtenerEjercicios(@Header("Authorization") String authorization);

    @GET("api/rutinas/detalle")
    Call<RptaDetalleRutina> detalleRutina(
        @Header("Authorization") String authorization,
        @Query("rutina_id") int rutinaId
    );

    @HTTP(method = "DELETE", path = "api/rutinas/eliminar_ejercicio", hasBody = true)
    Call<RptaRegistro> eliminarEjercicioDeRutina(@Header("Authorization") String authorization, @Body ReqEliminarEjercicio req);

    @GET("api/perfilcliente")
    Call<RptaPerfilCliente> obtenerPerfilCliente(@Header("Authorization") String authorization);

    @PUT("api/editarperfil")
    Call<RptaPerfilCliente> editarPerfil(@Header("Authorization") String authorization, @Body ReqEditarPerfil reqEditarPerfil);

    @POST("api/recuperar/enviar_codigo")
    Call<RptaRegistro> enviarCodigoRecuperacion(@Body ReqRecuperarCorreo req);

    @POST("api/recuperar/verificar_codigo")
    Call<RptaRegistro> verificarCodigoRecuperacion(@Body ReqRecuperarCodigo req);

    @POST("api/recuperar/nueva_contrasena")
    Call<RptaRegistro> nuevaContrasenaRecuperacion(@Body ReqRecuperarNuevaContrasena req);

    @POST("api/logout")
    Call<RptaRegistro> logout(@Header("Authorization") String authorization);
}
