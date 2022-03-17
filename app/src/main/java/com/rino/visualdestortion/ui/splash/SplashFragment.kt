package com.rino.visualdestortion.ui.splash


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentSplashBinding
import com.rino.visualdestortion.ui.home.MainActivity
import com.rino.visualdestortion.utils.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private val SPLASH_TIME_OUT = 3000L
    private lateinit var binding: FragmentSplashBinding
    private lateinit var viewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = true

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = SplashViewModel(requireActivity().application)
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        setAnimation()
        splashSetup()
        observeData()


        return binding.root
    }

    private fun observeData() {
        observeIsPrepared()
        observeShowError()
    }

    private fun observeIsPrepared() {
        viewModel.isPrepared.observe(viewLifecycleOwner) {
            if (!viewModel.isLogin()){
                navToWelcome()
            }
            else {
                if (it) {
                    navToHome()
                    //   navigateToDailyPreparation()
                } else {
                    navigateToDailyPreparation()
                    //    navToHome()
                }
            }
        }
    }
    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(getResources().getColor(R.color.teal))
                    .setActionTextColor(getResources().getColor(R.color.white)).setAction("Ok")
                    {
                    }.show()
            }
        }
    }

    private fun navToHome() {
        val action = SplashFragmentDirections.actionSplashToServiceFragment()
        findNavController().navigate(action)
    }

    private fun navigateToDailyPreparation() {
        val action = SplashFragmentDirections.actionSplashToDailyPreparation()
        findNavController().navigate(action)
    }

    private fun navToWelcome() {
        val action = SplashFragmentDirections.actionSplashToWelcome()
        findNavController().navigate(action)
    }

    private fun splashSetup(){
        CoroutineScope(Dispatchers.Default).launch{
            delay(SPLASH_TIME_OUT)
            CoroutineScope(Dispatchers.Main).launch{
                if (viewModel.isLogin()) {
                    if(NetworkConnection.checkInternetConnection(requireContext())) {
                        viewModel.isTodayPrepared()
                    }
                    else{
                        showMessage(getString(R.string.no_internet))
                    }
                }
                else{
                    navToWelcome()
                }
//                findNavController().popBackStack()
//                findNavController().navigate(R.id.welcomeFragment)
            }
        }
    }

    private fun setAnimation() {
        binding.part2Txt.animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.down_to_up
        )
        binding.logoImg.animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.up_to_down
        )
        binding.part1Txt.animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.left_to_right
        )
        binding.part3Txt.animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.right_to_left
        )
    }
    private fun showMessage(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_INDEFINITE)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                resources.getColor(
                    R.color.teal
                )
            )
            .setActionTextColor(resources.getColor(R.color.white)).setAction(getString(
                R.string.dismiss))
            {
            }.show()
    }


}