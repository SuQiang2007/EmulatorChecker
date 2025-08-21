using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Test : MonoBehaviour
{
    [SerializeField]
    private Text text;
    
    [SerializeField]
    private AARCaller aarCaller;
    // Start is called before the first frame update
    void Start()
    {
        CallAARMethod();
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    private AndroidJavaObject m_MainActivity;
    private void CallAARMethod()
    {
        bool isEmu = IsEmulator();
        string brand = GetPhoneBrand();
        string model = GetPhoneModel();
        string hardware = CheckFeaturesByHardware();
        text.text = $"isEmulator: {isEmu}\nbrand: {brand}\nmodel: {model}\nhardware: {hardware}";
    }
    
    public static bool IsEmulator()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
            using (var unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
            using (var activity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity"))
            using (var checker = new AndroidJavaClass("com.squall.checker.Checker"))
            {
                return checker.CallStatic<bool>("isEmulator", activity);
            }
#else
        return false;
#endif
    }

    public static string CheckFeaturesByHardware()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
            using (var unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
            using (var activity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity"))
            using (var checker = new AndroidJavaClass("com.squall.checker.Checker"))
            {
                return checker.CallStatic<string>("checkFeaturesByHardware", activity);
            }
#else
        return "editor";
#endif
    }

    public static string GetPhoneBrand()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
            using (var checker = new AndroidJavaClass("com.squall.checker.Checker"))
            {
                return checker.CallStatic<string>("getPhoneBrand");
            }
#else
        return "editor";
#endif
    }

    public static string GetPhoneModel()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
            using (var checker = new AndroidJavaClass("com.squall.checker.Checker"))
            {
                return checker.CallStatic<string>("getPhoneModel");
            }
#else
        return "editor";
#endif
    }
} 