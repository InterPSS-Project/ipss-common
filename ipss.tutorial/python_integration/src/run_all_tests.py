"""
Test runner for all InterPSS Python integration examples.
Runs each script independently and reports results.
"""

import subprocess
import sys
from pathlib import Path
import time
from typing import Dict, List, Tuple

# Color codes for terminal output
class Colors:
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def run_script(script_path: Path, timeout: int = 300) -> Tuple[bool, float, str]:
    """
    Run a single Python script and capture results.
    
    Args:
        script_path: Path to the Python script
        timeout: Maximum time to wait for script (seconds)
    
    Returns:
        Tuple of (success, duration, error_message)
    """
    print(f"\n{Colors.BLUE}Running: {script_path.name}{Colors.RESET}")
    start_time = time.time()
    
    try:
        result = subprocess.run(
            [sys.executable, str(script_path)],
            capture_output=True,
            text=True,
            timeout=timeout,
            cwd=script_path.parent
        )
        
        duration = time.time() - start_time
        
        if result.returncode == 0:
            print(f"{Colors.GREEN}✓ PASSED{Colors.RESET} ({duration:.2f}s)")
            return True, duration, ""
        else:
            error_msg = result.stderr if result.stderr else result.stdout
            print(f"{Colors.RED}✗ FAILED{Colors.RESET} ({duration:.2f}s)")
            print(f"  Error: {error_msg[:200]}...")
            return False, duration, error_msg
            
    except subprocess.TimeoutExpired:
        duration = time.time() - start_time
        print(f"{Colors.RED}✗ TIMEOUT{Colors.RESET} ({timeout}s)")
        return False, duration, f"Script timed out after {timeout} seconds"
    except Exception as e:
        duration = time.time() - start_time
        print(f"{Colors.RED}✗ ERROR{Colors.RESET} ({duration:.2f}s)")
        print(f"  Error: {str(e)}")
        return False, duration, str(e)

def main():
    """Main test runner function."""
    
    # Define test scripts and their configurations
    scripts_config = [
        # Basic examples (fast, should run first)
        {
            'name': 'sample_psse.py',
            'description': 'Basic PSSE IEEE9Bus example',
            'timeout': 60,
            'category': 'basic'
        },
        {
            'name': 'run_pf_texas2k.py',
            'description': 'Texas 2000-bus power flow',
            'timeout': 120,
            'category': 'medium'
        },
        {
            'name': 'run_pf_texas2k_zbr.py',
            'description': 'Texas 2000-bus with ZBR model',
            'timeout': 120,
            'category': 'medium'
        },
        {
            'name': 'run_pf_kundur_vschvdc.py',
            'description': 'Kundur 2-area VSC HVDC',
            'timeout': 60,
            'category': 'basic'
        },
        {
            'name': 'run_pf_kundur_lcchvdc.py',
            'description': 'Kundur 2-area LCC HVDC',
            'timeout': 60,
            'category': 'basic'
        },
        # Contingency analysis (slower)
        {
            'name': 'run_aclf_contingency_texas2k.py',
            'description': 'AC contingency analysis - Texas 2k',
            'timeout': 300,
            'category': 'contingency'
        },
        {
            'name': 'run_dclf_contingency_texas2k.py',
            'description': 'DC contingency analysis - Texas 2k',
            'timeout': 300,
            'category': 'contingency'
        },
          {
            'name': 'run_dclf_contingencyMonad_texas2k.py',
            'description': 'Monad based DC contingency analysis - Texas 2k',
            'timeout': 300,
            'category': 'contingency'
        },
        # Large system examples (very slow - optional)
        {
            'name': 'run_pf_ACTIVSg25k.py',
            'description': 'ACTIVSg 25k-bus power flow',
            'timeout': 300,
            'category': 'large',
            'skip_by_default': True
        },
        {
            'name': 'run_aclf_contingency_ACTIVSg25k.py',
            'description': 'AC contingency analysis - ACTIVSg 25k',
            'timeout': 600,
            'category': 'large',
            'skip_by_default': True
        },
    ]
    
    script_dir = Path(__file__).parent
    
    # Parse command line arguments
    run_all = '--all' in sys.argv
    run_large = '--large' in sys.argv or run_all
    verbose = '--verbose' in sys.argv
    specific_test = None
    
    for arg in sys.argv[1:]:
        if not arg.startswith('--') and arg.endswith('.py'):
            specific_test = arg
    
    # Print header
    print(f"\n{Colors.BOLD}{'='*70}{Colors.RESET}")
    print(f"{Colors.BOLD}InterPSS Python Integration Test Suite{Colors.RESET}")
    print(f"{Colors.BOLD}{'='*70}{Colors.RESET}\n")
    
    if specific_test:
        print(f"Running specific test: {specific_test}\n")
    elif run_large:
        print(f"{Colors.YELLOW}Running ALL tests including large systems{Colors.RESET}\n")
    else:
        print(f"Running standard tests (use --all or --large to include large systems)\n")
    
    # Filter scripts based on options
    scripts_to_run = []
    for config in scripts_config:
        script_path = script_dir / config['name']
        
        # Skip if specific test requested and this isn't it
        if specific_test and config['name'] != specific_test:
            continue
            
        # Skip large tests unless explicitly requested
        if config.get('skip_by_default', False) and not run_large and not specific_test:
            print(f"{Colors.YELLOW}⊘ SKIPPED{Colors.RESET}: {config['name']} (use --large to run)")
            continue
        
        # Check if script exists
        if not script_path.exists():
            print(f"{Colors.YELLOW}⚠ MISSING{Colors.RESET}: {config['name']}")
            continue
            
        scripts_to_run.append((script_path, config))
    
    if not scripts_to_run:
        print(f"\n{Colors.RED}No scripts to run!{Colors.RESET}")
        print("\nUsage:")
        print(f"  python {Path(__file__).name}              # Run standard tests")
        print(f"  python {Path(__file__).name} --all        # Run all tests including large systems")
        print(f"  python {Path(__file__).name} --large      # Include large system tests")
        print(f"  python {Path(__file__).name} --verbose    # Show detailed output")
        print(f"  python {Path(__file__).name} script.py   # Run specific test")
        return 1
    
    # Run tests
    results: Dict[str, Tuple[bool, float, str]] = {}
    total_start = time.time()
    
    for script_path, config in scripts_to_run:
        print(f"\n{Colors.BOLD}[{config['category'].upper()}]{Colors.RESET} {config['description']}")
        success, duration, error = run_script(script_path, config['timeout'])
        results[config['name']] = (success, duration, error)
        
        if verbose and error:
            print(f"\n{Colors.YELLOW}Full error output:{Colors.RESET}")
            print(error)
    
    total_duration = time.time() - total_start
    
    # Print summary
    print(f"\n{Colors.BOLD}{'='*70}{Colors.RESET}")
    print(f"{Colors.BOLD}Test Results Summary{Colors.RESET}")
    print(f"{Colors.BOLD}{'='*70}{Colors.RESET}\n")
    
    passed = sum(1 for success, _, _ in results.values() if success)
    failed = len(results) - passed
    
    print(f"Total tests run: {len(results)}")
    print(f"{Colors.GREEN}Passed: {passed}{Colors.RESET}")
    print(f"{Colors.RED}Failed: {failed}{Colors.RESET}")
    print(f"Total time: {total_duration:.2f}s\n")
    
    if failed > 0:
        print(f"{Colors.RED}Failed tests:{Colors.RESET}")
        for name, (success, duration, error) in results.items():
            if not success:
                print(f"  • {name} ({duration:.2f}s)")
                if error and not verbose:
                    # Show first line of error
                    first_line = error.split('\n')[0]
                    print(f"    {first_line[:100]}")
        print(f"\n{Colors.YELLOW}Tip: Run with --verbose to see full error messages{Colors.RESET}")
    
    print()
    return 0 if failed == 0 else 1

if __name__ == '__main__':
    sys.exit(main())
